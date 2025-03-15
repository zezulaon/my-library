package dev.zezula.books.data

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.asNetworkBook
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.note.asNetworkNote
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.model.shelf.asNetworkShelf
import dev.zezula.books.data.model.shelf.asNetworkShelfWithBook
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SyncLibraryRepositoryImpl(
    private val bookDao: BookDao,
    private val shelfAndBookDao: ShelfAndBookDao,
    private val noteDao: NoteDao,
    private val shelvesAndBooksDao: ShelfAndBookDao,
) : SyncLibraryRepository {

    override fun getAllBooksPendingSyncFlow(): Flow<List<NetworkBook>> {
        return bookDao
            .getAllPendingSyncBooksFlow()
            .map { list ->
                list.map(BookEntity::asNetworkBook)
            }
    }

    override suspend fun resetBookPendingSyncStatus(bookId: String) {
        bookDao.resetBookPendingSyncStatus(bookId)
    }

    override fun getAllShelvesWithBooksPendingSyncFlow(): Flow<List<NetworkShelfWithBook>> {
        return shelfAndBookDao
            .getAllShelvesWithBooksPendingSyncFlow()
            .map { list ->
                list.map(ShelfWithBookEntity::asNetworkShelfWithBook)
            }
    }

    override suspend fun resetShelfWithBookPendingSyncStatus(shelfId: String, bookId: String) {
        shelfAndBookDao.resetShelvesWithBooksPendingSyncStatus(shelfId = shelfId, bookId = bookId)
    }

    override fun getAllNotesPendingSyncFlow(): Flow<List<NetworkNote>> {
        return noteDao
            .getAllPendingSyncStream()
            .map { list ->
                list.map(NoteEntity::asNetworkNote)
            }
    }

    override suspend fun resetNotePendingSyncStatus(noteId: String) {
        noteDao.resetPendingSyncStatus(noteId)
    }

    override fun getAllShelvesPendingSyncFlow(): Flow<List<NetworkShelf>> {
        return shelvesAndBooksDao
            .getAllPendingSyncShelvesStream()
            .map { list ->
                list.map(ShelfEntity::asNetworkShelf)
            }
    }

    override suspend fun resetShelfPendingSyncStatus(shelfId: String) {
        shelvesAndBooksDao.resetPendingSyncStatus(shelfId)
    }
}
