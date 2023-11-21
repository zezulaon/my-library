package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.LibraryBookEntity
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.asNetworkBook
import dev.zezula.books.data.model.book.fromNetworkBook
import dev.zezula.books.data.model.note.fromNetworkNote
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.model.shelf.fromNetworkShelf
import dev.zezula.books.data.model.shelf.fromNetworkShelfWithBook
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
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
        addOrUpdateNetworkBook(existingBook.asNetworkBook())
        bookDao.addToLibraryBooks(LibraryBookEntity(bookId))
    }

    override fun isBookInLibrary(bookId: String): Flow<Boolean> {
        return bookDao.getLibraryBookStream(bookId).map { it != null }
    }

    override suspend fun deleteBookFromLibrary(bookId: String) {
        networkDataSource.deleteBook(bookId)

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
        addOrUpdateNetworkBook(networkBook)
        return networkBook
    }

    private suspend fun addOrUpdateNetworkBook(
        networkBook: NetworkBook,
    ): NetworkBook {
        networkDataSource.addOrUpdateBook(networkBook)
        return networkBook
    }

    override suspend fun updateBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean) {
        // Check if the book is part of user's library. If not, do not allow to add it to a shelf.
        val libraryBook = bookDao.getLibraryBookStream(bookId).firstOrNull()
        checkNotNull(libraryBook) { "Cannot modify the shelf -> book with id: [$bookId] is not in the library." }

        val shelvesWithBooksEntity = ShelfWithBookEntity(bookId = bookId, shelfId = shelfId)

        networkDataSource.updateBookInShelf(shelfId, bookId, isBookInShelf)

        if (isBookInShelf) {
            shelfAndBookDao.addBookToShelf(shelvesWithBooksEntity)
        } else {
            shelfAndBookDao.removeBookFromShelf(shelvesWithBooksEntity)
        }
    }

    override suspend fun refreshBooks() {
        val numberOfBooks = bookDao.getBookCount()
        // TODO: implement proper syncing. Right now, the firestore is used as a simple online "back up" (which is
        //  downloaded only when there are no books in the app database)
        if (numberOfBooks == 0) {
            networkDataSource.getBooks().forEach { networkBook ->
                val bookEntity = fromNetworkBook(networkBook)
                bookDao.addOrUpdate(bookEntity)

                // Associate the book with the library
                bookDao.addToLibraryBooks(LibraryBookEntity(bookId = bookEntity.id))

                networkDataSource.getNotesForBook(bookEntity.id).forEach { networkNote ->
                    val networkNoteEntity = fromNetworkNote(
                        networkNote = networkNote,
                        bookId = bookEntity.id,
                    )
                    noteDao.addOrUpdateNote(networkNoteEntity)
                }
            }
            networkDataSource.getShelves().forEach { networkShelf ->
                shelfAndBookDao.addOrUpdate(fromNetworkShelf(networkShelf))
            }
            networkDataSource.getShelvesWithBooks().forEach { networkShelfWithBook ->
                shelfAndBookDao.addBookToShelf(fromNetworkShelfWithBook(networkShelfWithBook))
            }
        }
    }

    override suspend fun searchMyLibraryBooks(query: String): List<Book> {
        return bookDao.getLibraryBooksForForQuery(query).map(BookEntity::asExternalModel)
    }
}
