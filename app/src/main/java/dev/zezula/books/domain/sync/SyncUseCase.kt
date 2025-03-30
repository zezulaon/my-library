package dev.zezula.books.domain.sync

import dev.zezula.books.data.model.book.asEntity
import dev.zezula.books.data.model.note.asEntity
import dev.zezula.books.data.model.shelf.asEntity
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.ShelfDao
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class SyncUseCase(
    private val bookDao: BookDao,
    private val noteDao: NoteDao,
    private val shelfDao: ShelfDao,
    private val shelfAndBookDao: ShelfAndBookDao,
    private val networkDataSource: NetworkDataSource,
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
        syncShelves()
        syncBooks()
        syncShelvesWithBooks()
        syncNotes()
    }

    private suspend fun syncShelves() {
        val lastModifiedTimestamp = shelfDao.getLatestLastModifiedTimestamp()
        val modifiedShelves = networkDataSource.getModifiedShelves(lastModifiedTimestamp)
        shelfDao.insertOrUpdateShelves(modifiedShelves.map { it.asEntity() })
    }

    private suspend fun syncShelvesWithBooks() {
        val lastModifiedTimestamp = shelfAndBookDao.getLatestLastModifiedTimestamp()
        val modifiedShelvesWithBooks = networkDataSource.getModifiedShelvesWithBooks(lastModifiedTimestamp)
        shelfAndBookDao.insertOrUpdateShelvesWithBooks(modifiedShelvesWithBooks.map { it.asEntity() })
    }

    private suspend fun syncNotes() {
        val lastModifiedTimestamp = noteDao.getLatestLastModifiedTimestamp()
        val modifiedNotes = networkDataSource.getModifiedNotes(lastModifiedTimestamp)
        noteDao.insertOrUpdateNotes(modifiedNotes.map { it.asEntity() })
    }

    private suspend fun syncBooks() {
        val lastModifiedTimestamp = bookDao.getLatestLastModifiedTimestamp()
        val modifiedBooks = networkDataSource.getModifiedBooks(lastModifiedTimestamp)
        bookDao.insertOrUpdateBooks(modifiedBooks.map { it.asEntity() })
    }
}