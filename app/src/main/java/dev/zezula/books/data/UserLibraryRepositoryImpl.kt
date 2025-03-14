package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.asBookEntity
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class UserLibraryRepositoryImpl(
    private val bookDao: BookDao,
    private val shelfAndBookDao: ShelfAndBookDao,
    private val noteDao: NoteDao,
) : UserLibraryRepository {

    override fun getAllLibraryBooksStream(): Flow<List<Book>> {
        return bookDao.getAllLibraryBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getAllLibraryPendingSyncBooksStream(): Flow<List<Book>> {
        return bookDao.getAllLibraryPendingSyncBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getAllShelvesWithBooksPendingSyncStream(): Flow<List<ShelfWithBookEntity>> {
        return shelfAndBookDao.getAllPendingShelvesWithBooksStream()
    }

    override fun isBookDeleted(bookId: String): Flow<Boolean> {
        return bookDao.getBookStream(bookId).map { it?.isDeleted == true}
    }

    override suspend fun resetPendingSyncStatus(bookId: String) {
        bookDao.resetPendingSyncStatus(bookId)
    }

    override suspend fun resetShelvesWithBooksSyncStatus(shelfId: String, bookId: String) {
        shelfAndBookDao.resetShelvesWithBooksPendingSyncStatus(shelfId = shelfId, bookId = bookId)
    }

    override suspend fun moveBookToLibrary(bookId: String) {
        val existingBook = bookDao.getBookStream(bookId).firstOrNull()
        checkNotNull(existingBook) { "Failed to add book to Library -> book with id: [$bookId] does not exist." }
        bookDao.addToLibraryBooks(bookId = bookId, dateAdded = LocalDateTime.now().toString())
    }

    override fun isBookInLibrary(bookId: String): Flow<Boolean> {
        return bookDao.isBookInLibrary(bookId)
    }

    override suspend fun deleteBookFromLibrary(bookId: String) {
        bookDao.softDeleteFromLibraryBooks(bookId)
        bookDao.setPendingSyncStatus(bookId)

        noteDao.softDeleteNotesForBook(bookId)

        shelfAndBookDao.softDeleteShelvesWithBooksForBook(bookId)
    }

    override fun getBooksForShelfStream(shelfId: String): Flow<List<Book>> {
        return shelfAndBookDao.getBooksForShelfStream(shelfId).map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun addBook(bookFormData: BookFormData): Book {
        val createdId = UUID.randomUUID().toString()
        val book = addOrUpdateBook(createdId, bookFormData)

        // Associate the book with the library
        moveBookToLibrary(book.id)

        return book
    }

    override suspend fun addOrUpdateBook(bookId: String, bookFormData: BookFormData): Book {
        Timber.d("addOrUpdateBook($bookId, $bookFormData)")
        // FIXME: simplify/improve upserting and inserting
        val isBookInLibrary = bookDao.isBookInLibrary(bookId).first()
        val bookEntity = bookFormData
            .asBookEntity(id = bookId)
            .copy(isInLibrary = isBookInLibrary, isPendingSync = isBookInLibrary)
        bookDao.addOrUpdate(bookEntity)

        return bookEntity.asExternalModel()
    }

    override suspend fun updateBookCover(bookId: String, thumbnailLink: String) {
        bookDao.updateBookCover(bookId, thumbnailLink)
        bookDao.setPendingSyncStatus(bookId)
    }

    override suspend fun updateBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean) {
        // Check if the book is part of user's library. If not, do not allow to add it to a shelf.
        val isBookInLibrary = isBookInLibrary(bookId).first()
        check(isBookInLibrary) { "Cannot modify the shelf -> book with id: [$bookId] is not in the library." }

        val shelvesWithBooksEntity = ShelfWithBookEntity(bookId = bookId, shelfId = shelfId, isPendingSync = true, isDeleted = isBookInShelf.not())
        shelfAndBookDao.addBookToShelf(shelvesWithBooksEntity)
    }

    override suspend fun refreshBooks() {
        val numberOfBooks = bookDao.getBookCount()
        // FIXME: implement proper syncing. Right now, the firestore is used as a simple online "back up" (which is
        //  downloaded only when there are no books in the app database)
//        if (numberOfBooks == 0) {
//            networkDataSource.getBooks().forEach { networkBook ->
//                val bookEntity = fromNetworkBook(networkBook)
//                bookDao.addOrUpdate(bookEntity)
//
//                // Associate the book with the library
//                bookDao.addToLibraryBooks(LibraryBookEntity(bookId = bookEntity.id))
//
//                networkDataSource.getNotesForBook(bookEntity.id).forEach { networkNote ->
//                    val networkNoteEntity = fromNetworkNote(
//                        networkNote = networkNote,
//                        bookId = bookEntity.id,
//                    )
//                    noteDao.addOrUpdateNote(networkNoteEntity)
//                }
//            }
//            networkDataSource.getShelves().forEach { networkShelf ->
//                shelfAndBookDao.addOrUpdate(fromNetworkShelf(networkShelf))
//            }
//            networkDataSource.getShelvesWithBooks().forEach { networkShelfWithBook ->
//                shelfAndBookDao.addBookToShelf(fromNetworkShelfWithBook(networkShelfWithBook))
//            }
//        }
    }

    override suspend fun searchMyLibraryBooks(query: String): List<Book> {
        return bookDao.getLibraryBooksForQuery(query).map(BookEntity::asExternalModel)
    }
}
