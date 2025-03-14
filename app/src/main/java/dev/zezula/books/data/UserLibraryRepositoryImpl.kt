package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.asNetworkBook
import dev.zezula.books.data.model.book.fromNetworkBook
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
    private val networkDataSource: NetworkDataSource,
) : UserLibraryRepository {

    override fun getAllLibraryBooksStream(): Flow<List<Book>> {
        return bookDao.getAllLibraryBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun moveBookToLibrary(bookId: String) {
        val existingBook = bookDao.getBookStream(bookId).firstOrNull()
        checkNotNull(existingBook) { "Failed to add book to Library -> book with id: [$bookId] does not exist." }
        // FIXME: implement proper syncing.
//        addOrUpdateNetworkBook(existingBook.asNetworkBook())
        bookDao.addToLibraryBooks(bookId = bookId, dateAdded = LocalDateTime.now().toString())
    }

    override fun isBookInLibrary(bookId: String): Flow<Boolean> {
        return bookDao.isBookInLibrary(bookId)
    }

    override suspend fun deleteBookFromLibrary(bookId: String) {
        // FIXME: implement proper syncing.
//        networkDataSource.deleteBook(bookId)

        // Delete the book from the DB. Associated library reference will be deleted by the DB cascade
        bookDao.delete(bookId)
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
        val networkBook = addOrUpdateNetworkBook(bookId, bookFormData)
        val bookEntity = fromNetworkBook(networkBook)
        bookDao.addOrUpdate(bookEntity)
        return bookEntity.asExternalModel()
    }

    private suspend fun addOrUpdateNetworkBook(
        bookId: String,
        bookFormData: BookFormData,
    ): NetworkBook {
        val networkBook = bookFormData.asNetworkBook(bookId)
        // FIXME: implement proper syncing.
//        addOrUpdateNetworkBook(networkBook)
        return networkBook
    }

//    private suspend fun addOrUpdateNetworkBook(
//        networkBook: NetworkBook,
//    ): NetworkBook {
//        networkDataSource.addOrUpdateBook(networkBook)
//        return networkBook
//    }

    override suspend fun updateBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean) {
        // Check if the book is part of user's library. If not, do not allow to add it to a shelf.
        val isBookInLibrary = isBookInLibrary(bookId).first()
        check(isBookInLibrary) { "Cannot modify the shelf -> book with id: [$bookId] is not in the library." }

        val shelvesWithBooksEntity = ShelfWithBookEntity(bookId = bookId, shelfId = shelfId)

        // FIXME: implement proper syncing.
//        networkDataSource.updateBookInShelf(shelfId, bookId, isBookInShelf)

        if (isBookInShelf) {
            shelfAndBookDao.addBookToShelf(shelvesWithBooksEntity)
        } else {
            shelfAndBookDao.removeBookFromShelf(shelvesWithBooksEntity)
        }
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
