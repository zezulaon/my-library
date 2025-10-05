package dev.zezula.books.data.sync

import dev.zezula.books.data.asEntity
import dev.zezula.books.data.database.BookDao
import dev.zezula.books.data.database.NoteDao
import dev.zezula.books.data.database.ShelfAndBookDao
import dev.zezula.books.data.database.ShelfDao
import dev.zezula.books.data.network.api.NetworkDataSource
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.services.AuthService
import timber.log.Timber

class SyncUseCase(
    private val bookDao: BookDao,
    private val noteDao: NoteDao,
    private val shelfDao: ShelfDao,
    private val shelfAndBookDao: ShelfAndBookDao,
    private val networkDataSource: NetworkDataSource,
    private val authService: AuthService,
) {

    suspend operator fun invoke(): Response<Unit> {
        return asResponse {
            syncLibrary()
        }
            .onError {
                Timber.e(it, "Failed to refresh library.")
            }
    }

    private suspend fun syncLibrary() {
        if (authService.isUserSignedIn().not()) {
            Timber.d("User is not logged in. Skipping sync.")
            return
        }

        syncShelves()
        syncBooks()
        syncShelvesWithBooks()
        syncNotes()
    }

    private suspend fun syncShelves() {
        val lastModifiedTimestamp = shelfDao.getLatestLastModifiedTimestamp()
        val modifiedShelves = networkDataSource.getModifiedShelves(lastModifiedTimestamp)
        shelfDao.insertOrUpdateShelves(modifiedShelves.mapNotNull { it.asEntity() })
    }

    private suspend fun syncShelvesWithBooks() {
        val lastModifiedTimestamp = shelfAndBookDao.getLatestLastModifiedTimestamp()
        val modifiedShelvesWithBooks = networkDataSource.getModifiedShelvesWithBooks(lastModifiedTimestamp)

        shelfAndBookDao.insertOrUpdateShelvesWithBooks(modifiedShelvesWithBooks.mapNotNull { it.asEntity() })
    }

    private suspend fun syncNotes() {
        val lastModifiedTimestamp = noteDao.getLatestLastModifiedTimestamp()
        val modifiedNotes = networkDataSource.getModifiedNotes(lastModifiedTimestamp)
        noteDao.insertOrUpdateNotes(modifiedNotes.mapNotNull { it.asEntity() })
    }

    private suspend fun syncBooks() {
        val lastModifiedTimestamp = bookDao.getLatestLastModifiedTimestamp()
        val modifiedBooks = networkDataSource.getModifiedBooks(lastModifiedTimestamp)
        bookDao.insertOrUpdateBooks(modifiedBooks.mapNotNull { it.asEntity() })
    }
}
