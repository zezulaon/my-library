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
            downloadLibrary()
        }
            .onError {
                Timber.e(it, "Failed to refresh library.")
            }
    }

    private suspend fun downloadLibrary() {
        val numberOfBooks = bookDao.getBookCount()
        Timber.d("Number of books in the app database: $numberOfBooks")
        Timber.d("Sync is required: ${numberOfBooks == 0}")
        // FIXME: implement proper syncing. Right now, the firestore is used as a simple online "back up" (which is
        //  downloaded only when there are no books in the app database)
        if (numberOfBooks == 0) {
            networkDataSource.getBooks().forEach { networkBook ->
                val bookEntity = networkBook.asEntity()

                bookDao.insertOrUpdate(bookEntity)

                networkDataSource.getNotesForBook(bookEntity.id).forEach { networkNote ->
                    val networkNoteEntity = networkNote.asEntity()
                    noteDao.insertOrUpdateNote(networkNoteEntity)
                }
            }
            networkDataSource.getShelves().forEach { networkShelf ->
                shelfDao.insertOrUpdateShelf(networkShelf.asEntity())
            }
            networkDataSource.getShelvesWithBooks().forEach { networkShelfWithBook ->
                shelfAndBookDao.insertOrUpdateShelfWithBook(networkShelfWithBook.asEntity())
            }
        }
    }
}