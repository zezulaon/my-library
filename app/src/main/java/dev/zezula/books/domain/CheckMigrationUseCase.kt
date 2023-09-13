package dev.zezula.books.domain

import dev.zezula.books.data.model.legacy.toBookFormData
import dev.zezula.books.data.model.user.NetworkMigrationData
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.legacy.LegacyBookDao
import dev.zezula.books.data.source.db.legacy.LegacyGroupShelfBookDao
import dev.zezula.books.data.source.db.legacy.LegacyShelfDao
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

data class MigrationProgress(
    val type: MigrationType,
    val total: Int,
    val current: Int,
)

enum class MigrationType {
    SHELVES,
    BOOKS,
    GROUPING,
}

class CheckMigrationUseCase(
    private val addOrUpdateBookUseCase: AddOrUpdateBookUseCase,
    private val updateShelfUseCase: UpdateShelfUseCase,
    private val toggleBookInShelfUseCase: ToggleBookInShelfUseCase,
    private val bookDao: BookDao,
    private val shelfDao: ShelfAndBookDao,
    private val legacyBookDao: LegacyBookDao,
    private val legacyShelfDao: LegacyShelfDao,
    private val legacyGroupShelfBookDao: LegacyGroupShelfBookDao,
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
                )
            )
        }
            .onError {
                Timber.e(it, "Failed to to migrate legacy DB.")
            }
    }

    private suspend fun migrateShelves(migrationProgress: MutableStateFlow<MigrationProgress?>) {
        val shelves = legacyShelfDao.getAllShelves()
        val existingShelves = shelfDao.getAllShelvesAsStream().firstOrNull()
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
                val existingShelf = existingShelves?.firstOrNull { it.id == id.toString() }
                if (existingShelf == null) {
                    updateShelfUseCase(id.toString(), title)
                        .fold(
                            onSuccess = {
                                Timber.d("Shelf migrated successfully")
                            },
                            onFailure = {
                                Timber.d("Failed to migrate the shelf")
                            },
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
                val existingBook = bookDao.getBook(book._id.toString()).firstOrNull()
                val yearStringLength = existingBook?.yearPublished?.toString()?.length ?: 0
                val isYearBroken = yearStringLength > 4
                if (existingBook == null || isYearBroken) {
                    addOrUpdateBookUseCase(bookId.toString(), book.toBookFormData())
                        .fold(
                            onSuccess = {
                                Timber.d("Book migrated successfully")
                            },
                            onFailure = {
                                Timber.d("Failed to migrate the book")
                            },
                        )
                } else {
                    Timber.d("Book already migrated")
                }
            }
        }
    }

    private suspend fun migrateGrouping(migrationProgress: MutableStateFlow<MigrationProgress?>) {
        val grouping = legacyGroupShelfBookDao.getAll()
        val existingGroupings = shelfDao.getAllShelfWithBookEntity()
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
                val bookExists = bookDao.getBook(volumeId.toString()).firstOrNull() != null
                val shelfExists = shelfDao.getAllShelvesAsStream()
                    .firstOrNull()?.any { it.id == shelfId.toString() } == true
                if (bookExists && shelfExists) {
                    // Check if the group is already migrated
                    val existingGrouping = existingGroupings.firstOrNull {
                        it.bookId == volumeId.toString() && it.shelfId == shelfId.toString()
                    }
                    if (existingGrouping == null) {
                        toggleBookInShelfUseCase(volumeId.toString(), shelfId.toString(), true)
                            .fold(
                                onSuccess = {
                                    Timber.d("Group migrated successfully")
                                },
                                onFailure = {
                                    Timber.d("Failed to migrate the group")
                                },
                            )
                    } else {
                        Timber.d("Group already migrated")
                    }
                } else {
                    Timber.w("Group not migrated. Book or shelf missing.")
                }
            }
        }
    }
}
