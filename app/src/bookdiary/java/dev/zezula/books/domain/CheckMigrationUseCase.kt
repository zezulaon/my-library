package dev.zezula.books.domain

import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import dev.zezula.books.BuildConfig
import dev.zezula.books.data.model.MigrationProgress
import dev.zezula.books.data.model.MigrationType
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.legacy.LegacyBookEntity
import dev.zezula.books.data.model.legacy.toBookEntity
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.model.user.NetworkMigrationData
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.ShelfDao
import dev.zezula.books.data.source.db.legacy.LegacyAppDatabase
import dev.zezula.books.data.source.db.legacy.LegacyBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.measureTime

class CheckMigrationUseCase(
    private val bookDao: BookDao,
    private val shelfAndBookDao: ShelfAndBookDao,
    private val shelfDao: ShelfDao,
    private val noteDao: NoteDao,
    private val networkDataSource: NetworkDataSource,
    private val legacyAppDatabase: LegacyAppDatabase,
    private val legacyBookDao: LegacyBookDao,
) {

    suspend operator fun invoke(migrationProgress: MutableStateFlow<MigrationProgress?>): Response<Unit> {
        return asResponse {
            Timber.d("Checking migration state...")

            val migrationData = networkDataSource.getMigrationData()
            if (migrationData.legacyDbMigrated == true) {
                Timber.d("Legacy DB already migrated. No additional action required.")
                return@asResponse
            }

            var errorMessage: String? = null
            var durationInSeconds: String? = null
            try {
                val duration = measureTime {
                    migrateShelves(migrationProgress)
                    migrateBooks(migrationProgress)
                    migrateGrouping(migrationProgress)

                    migrationProgress.value = MigrationProgress(
                        type = MigrationType.COMMENTS,
                        total = 0,
                        current = 0,
                    )
                    migrateComments()
                    migrateQuotes()
                }
                durationInSeconds = duration.inWholeSeconds.toString()
            } catch (e: Exception) {
                Timber.e(e, "Failed to migrate legacy DB.")
                errorMessage = e.message
            }

            networkDataSource.updateMigrationData(
                NetworkMigrationData(
                    legacyDbMigrated = errorMessage == null,
                    legacyAppId = BuildConfig.APPLICATION_ID,
                    migrationError = errorMessage,
                    versionCode = BuildConfig.VERSION_CODE.toString(),
                    migrationDurationInSeconds = durationInSeconds,
                ),
            )
        }
            .onError {
                Timber.e(it, "Failed to to migrate legacy DB.")
            }
    }

    private suspend fun migrateQuotes() {
        val db = legacyAppDatabase.openHelper.readableDatabase
        val cursor = try {
            db.query("SELECT * FROM quotations")
        } catch (e: Exception) {
            Timber.w(e, "Failed to migrate quotes")
            null
        } ?: return

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("_id"))
                val bookId = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("bookId"))
                val author = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("author"))
                val page = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("page"))
                val text = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("text"))

                if (id != null && bookId != null) {
                    val noteId = "${bookId}_${id}_quote"
                    noteDao.insertNote(
                        NoteEntity(
                            id = Note.Id(noteId),
                            bookId = Book.Id(bookId.toString()),
                            dateAdded = LocalDateTime.now().toString(),
                            text = text ?: "",
                            page = page?.toIntOrNull(),
                            type = "quote",
                            isPendingSync = true,
                            lastModifiedTimestamp = Clock.System.now().toString(),
                        )
                    )
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private suspend fun migrateComments() {
        val db = legacyAppDatabase.openHelper.readableDatabase
        val cursor = try {
            db.query("SELECT * FROM comments")
        } catch (e: Exception) {
            Timber.w(e, "Failed to migrate comments")
            null
        } ?: return

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("_id"))
                val bookId = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("bookId"))
                val dateCreated = cursor.getLongOrNull(cursor.getColumnIndexOrThrow("dateCreated"))
                val text = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("text"))

                val dateAdded = if (dateCreated != null) {
                    LocalDateTime.ofEpochSecond(dateCreated / 1000, 0, ZoneOffset.UTC)
                } else {
                    null
                }
                if (id != null && bookId != null) {
                    val noteId = "${bookId}_${id}_comment"
                    noteDao.insertNote(
                        NoteEntity(
                            id = Note.Id(noteId),
                            bookId = Book.Id(bookId.toString()),
                            dateAdded = dateAdded?.toString() ?: LocalDateTime.now().toString(),
                            text = text ?: "",
                            isPendingSync = true,
                            lastModifiedTimestamp = Clock.System.now().toString(),
                        )
                    )
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    enum class LegacyShelfType(val shelfId: String, val title: String) {
        HAVE_READ("legacyShelf_haveRead", "Have Read"),
        READING_NOW("legacyShelf_readingNow", "Reading Now"),
        TO_READ("legacyShelf_toRead", "To Read"),
        FAVORITE("legacyShelf_favorite", "Favorite"),
        IS_OWN("legacyShelf_iOwn", "I Own"),
        TO_BUY("legacyShelf_toBuy", "Want to Buy/Own"),
        LENT_TO("legacyShelf_lentTo", "Lent To"),
    }

    private suspend fun migrateShelves(migrationProgress: MutableStateFlow<MigrationProgress?>) {
        Timber.d("Migrating legacy shelves...")
        val legacyShelvesSize = LegacyShelfType.entries.size
        LegacyShelfType.entries.forEachIndexed { index, legacyShelfType ->
            migrationProgress.value = MigrationProgress(
                type = MigrationType.SHELVES,
                total = legacyShelvesSize,
                current = index + 1,
            )
            shelfDao.insertShelf(
                ShelfEntity(
                    id = legacyShelfType.shelfId,
                    dateAdded = LocalDateTime.now().toString(),
                    title = legacyShelfType.title,
                    isPendingSync = true,
                    lastModifiedTimestamp = Clock.System.now().toString(),
                )
            )
        }
        Timber.d("Migrating real shelves...")
        val shelves = legacyBookDao.getAllShelves()
        shelves.forEachIndexed { index, shelf ->
            migrationProgress.value = MigrationProgress(
                type = MigrationType.SHELVES,
                total = shelves.size + legacyShelvesSize,
                current = index + 1 + legacyShelvesSize,
            )

            Timber.d("Migrating shelf: $shelf")
            try {
                val id = shelf._id
                val title = shelf.name
                if (id != null && title != null) {
                    shelfDao.insertShelf(
                        ShelfEntity(
                            id = id.toString(),
                            dateAdded = LocalDateTime.now().toString(),
                            title = title,
                            isPendingSync = true,
                            lastModifiedTimestamp = Clock.System.now().toString(),
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "Failed to migrate shelf: $shelf")
            }
        }
    }

    private suspend fun migrateBooks(migrationProgress: MutableStateFlow<MigrationProgress?>) {
        Timber.d("Migrating books...")
        val books = legacyBookDao.getAll()?.shuffled()
        books?.forEachIndexed { index, book ->
            migrationProgress.value = MigrationProgress(
                type = MigrationType.BOOKS,
                total = books.size,
                current = index + 1,
            )
            Timber.d("Migrating book: $book")
            try {
                // Check if the book is already migrated
                val existingBook = bookDao.getBookFlow(Book.Id(book._id.toString())).firstOrNull()
                if (existingBook != null) {
                    Timber.d("Book already migrated. Skipping...")
                } else {
                    migrateBook(book)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to migrate book: ${book._id}")
            }
        }
    }

    private suspend fun migrateBook(book: LegacyBookEntity) {
        val bookId = book._id
        if (bookId != null) {
            val bookEntity = book.toBookEntity(bookId = bookId.toString())
            bookDao.insertBook(bookEntity)

            val state = legacyBookDao.getStatesForBookId(bookId)?.firstOrNull()
            if (state != null) {
                if (state.favorite == 1) addBookToShelf(LegacyShelfType.FAVORITE.shelfId, bookId.toString())
                if (state.haveRead == 1) addBookToShelf(LegacyShelfType.HAVE_READ.shelfId, bookId.toString())
                if (state.readingNow == 1) addBookToShelf(LegacyShelfType.READING_NOW.shelfId, bookId.toString())
                if (state.iOwn == 1) addBookToShelf(LegacyShelfType.IS_OWN.shelfId, bookId.toString())
                if (state.toBuy == 1) addBookToShelf(LegacyShelfType.TO_BUY.shelfId, bookId.toString())
                if (state.toRead == 1) addBookToShelf(LegacyShelfType.TO_READ.shelfId, bookId.toString())
            }
            val lentToName = book.lentToName
            if (lentToName != null && lentToName.isNotEmpty()) {
                addBookToShelf(LegacyShelfType.LENT_TO.shelfId, bookId.toString())
                noteDao.insertNote(
                    NoteEntity(
                        id = Note.Id("${bookId}_lent_to_id"),
                        bookId = Book.Id(bookId.toString()),
                        dateAdded = LocalDateTime.now().toString(),
                        text = "Lent to $lentToName",
                        isPendingSync = true,
                        lastModifiedTimestamp = Clock.System.now().toString(),
                    )
                )
            }
        }
    }

    private suspend fun migrateGrouping(migrationProgress: MutableStateFlow<MigrationProgress?>) {
        val grouping = legacyBookDao.getAllGroups()
        grouping.forEachIndexed { index, group ->
            migrationProgress.value = MigrationProgress(
                type = MigrationType.GROUPING,
                total = grouping.size,
                current = index + 1,
            )

            Timber.d("Migrating group: $group")
            val bookId = group.bookId
            val shelfId = group.groupId
            addBookToShelf(shelfId = shelfId.toString(), bookId = bookId.toString())
        }
    }

    private suspend fun addBookToShelf(shelfId: String?, bookId: String?) {
        if (bookId != null && shelfId != null) {
            val bookExists = bookDao.getBookFlow(Book.Id(bookId)).firstOrNull() != null
            val shelfExists = shelfAndBookDao.getAllShelvesFlow()
                .firstOrNull()?.any { it.id == shelfId.toString() } == true
            if (bookExists && shelfExists) {
                val shelvesWithBooksEntity = ShelfWithBookEntity(
                    bookId = Book.Id(bookId),
                    shelfId = shelfId,
                    isPendingSync = true,
                    isDeleted = false,
                    lastModifiedTimestamp = Clock.System.now().toString(),
                )
                shelfAndBookDao.insertOrUpdateShelfWithBook(shelvesWithBooksEntity)
            }
//                toggleBookInShelfUseCase(bookId.toString(), shelfId.toString(), true)
//                    .fold(
//                        onSuccess = {
//                            Timber.d("Book added to shelf successfully")
//                        },
//                        onFailure = {
//                            Timber.w(it, "Failed to add book to shelf")
//                        },
//                    )
//            } else {
//                Timber.w("Legacy group not migrated. Book or shelf missing.")
//            }
        }
    }
}
