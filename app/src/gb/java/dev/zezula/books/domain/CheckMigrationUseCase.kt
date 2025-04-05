package dev.zezula.books.domain

import dev.zezula.books.data.model.MigrationProgress
import dev.zezula.books.data.model.MigrationType
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.legacy.toBookEntity
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.model.user.NetworkMigrationData
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.ShelfDao
import dev.zezula.books.data.source.db.legacy.LegacyBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import timber.log.Timber
import java.time.LocalDateTime

class CheckMigrationUseCase(
    private val bookDao: BookDao,
    private val shelfAndBookDao: ShelfAndBookDao,
    private val shelfDao: ShelfDao,
    private val legacyBookDao: LegacyBookDao,
    private val networkDataSource: NetworkDataSource,
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
            try {
                migrateShelves(migrationProgress)
                migrateBooks(migrationProgress)
                migrateGrouping(migrationProgress)
            } catch (e: Exception) {
                Timber.e(e, "Failed to migrate legacy DB.")
                errorMessage = e.message
            }

            networkDataSource.updateMigrationData(
                NetworkMigrationData(
                    legacyDbMigrated = true,
                    legacyAppId = "org.zezi.gb",
                    migrationError = errorMessage,
                ),
            )
        }
            .onError {
                Timber.e(it, "Failed to to migrate legacy DB.")
            }
    }

    private suspend fun migrateShelves(migrationProgress: MutableStateFlow<MigrationProgress?>) {
        val shelves = legacyBookDao.getAllShelves()
        val existingShelves = shelfAndBookDao.getAllShelvesFlow().firstOrNull()
        shelves.forEachIndexed { index, shelf ->
            migrationProgress.value = MigrationProgress(
                type = MigrationType.SHELVES,
                total = shelves.size,
                current = index + 1,
            )
            Timber.d("Migrating shelf: $shelf")
            val id = shelf._id
            val title = shelf.title
            if (id != null && title != null) {
                // Check if the shelf is already migrated
                val existingShelf = existingShelves?.firstOrNull { it.id.value == id.toString() }
                if (existingShelf == null) {
                    shelfDao.insertShelf(
                        ShelfEntity(
                            id = Shelf.Id(id.toString()),
                            dateAdded = LocalDateTime.now().toString(),
                            title = title,
                            isPendingSync = true,
                            lastModifiedTimestamp = Clock.System.now().toString(),
                        ),
                    )
                } else {
                    Timber.d("Shelf already migrated")
                }
            }
        }
    }

    private suspend fun migrateBooks(migrationProgress: MutableStateFlow<MigrationProgress?>) {
        val books = legacyBookDao.getAll()
        books.forEachIndexed { index, book ->
            migrationProgress.value = MigrationProgress(
                type = MigrationType.BOOKS,
                total = books.size,
                current = index + 1,
            )
            Timber.d("Migrating book: $book")
            val bookId = book._id
            if (bookId != null) {
                // Check if the book is already migrated
                val existingBook = bookDao.getBookFlow(Book.Id(book._id.toString())).firstOrNull()
                val yearStringLength = existingBook?.yearPublished?.toString()?.length ?: 0
                val isYearBroken = yearStringLength > 4
                if (existingBook == null || isYearBroken) {
                    val bookEntity = book.toBookEntity(bookId = bookId.toString())
                    bookDao.insertBook(bookEntity)
                } else {
                    Timber.d("Book already migrated")
                }
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
            val volumeId = group.volumeId
            val shelfId = group.shelfId
            if (volumeId != null && shelfId != null) {
                val bookExists = bookDao.getBookFlow(Book.Id(volumeId.toString())).firstOrNull() != null
                val shelfExists = shelfAndBookDao.getAllShelvesFlow()
                    .firstOrNull()?.any { it.id.value == shelfId.toString() } == true
                if (bookExists && shelfExists) {
                    val shelvesWithBooksEntity = ShelfWithBookEntity(
                        bookId = Book.Id(volumeId.toString()),
                        shelfId = Shelf.Id(shelfId.toString()),
                        isPendingSync = true,
                        isDeleted = false,
                        lastModifiedTimestamp = Clock.System.now().toString(),
                    )
                    shelfAndBookDao.insertOrUpdateShelfWithBook(shelvesWithBooksEntity)
                    // Check if the group is already migrated
                } else {
                    Timber.w("Group not migrated. Book or shelf missing.")
                }
            }
        }
    }
}
