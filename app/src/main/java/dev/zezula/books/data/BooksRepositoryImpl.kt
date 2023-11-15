package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.fromNetworkBook
import dev.zezula.books.data.model.note.fromNetworkNote
import dev.zezula.books.data.model.review.LibraryBookEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.model.shelf.fromNetworkShelf
import dev.zezula.books.data.model.shelf.fromNetworkShelfWithBook
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class BooksRepositoryImpl(
    private val bookDao: BookDao,
    private val shelfAndBookDao: ShelfAndBookDao,
    private val noteDao: NoteDao,
    private val networkDataSource: NetworkDataSource,
) : BooksRepository {

    override suspend fun addBookToLibrary(bookId: String) {
        bookDao.addToLibraryBooks(LibraryBookEntity(bookId = bookId))
    }

    override fun getAllLibraryBooksStream(): Flow<List<Book>> {
        return bookDao.getAllLibraryBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getAllBooksStream(): Flow<List<Book>> {
        return bookDao.getAllBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getBooksForShelfStream(shelfId: String): Flow<List<Book>> {
        return shelfAndBookDao.getBooksForShelfStream(shelfId).map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getBookStream(bookId: String): Flow<Book?> {
        return bookDao.getBookStream(bookId)
            .map {
                it?.asExternalModel()
            }
    }

    override suspend fun getBook(bookId: String): Book? {
        return getBookStream(bookId).first()
    }

    override suspend fun getBookId(isbn: String): String? {
        val dbBooks = bookDao.getForIsbn(isbn)
        return dbBooks.firstOrNull()?.id
    }

    override suspend fun addBook(bookFormData: BookFormData): Book {
        val createdId = UUID.randomUUID().toString()
        return addOrUpdateBook(createdId, bookFormData)
    }

    override suspend fun addOrUpdateBook(bookId: String, bookFormData: BookFormData): Book {
        Timber.d("addOrUpdateBook($bookId, $bookFormData)")
        val networkBook = NetworkBook(
            id = bookId,
            title = bookFormData.title,
            author = bookFormData.author,
            isbn = bookFormData.isbn,
            description = bookFormData.description,
            dateAdded = bookFormData.dateAdded ?: LocalDateTime.now().toString(),
            publisher = bookFormData.publisher,
            thumbnailLink = bookFormData.thumbnailLink,
            yearPublished = bookFormData.yearPublished,
            pageCount = bookFormData.pageCount,
            userRating = bookFormData.userRating,
        )
        networkDataSource.addOrUpdateBook(networkBook)

        val bookEntity = fromNetworkBook(networkBook)
        bookDao.addOrUpdate(bookEntity)
        return bookEntity.asExternalModel()
    }

    override suspend fun updateBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean) {
        val shelvesWithBooksEntity = ShelfWithBookEntity(bookId = bookId, shelfId = shelfId)

        networkDataSource.updateBookInShelf(shelfId, bookId, isBookInShelf)

        if (isBookInShelf) {
            shelfAndBookDao.addBookToShelf(shelvesWithBooksEntity)
        } else {
            shelfAndBookDao.removeBookFromShelf(shelvesWithBooksEntity)
        }
    }

    override suspend fun deleteBook(bookId: String) {
        networkDataSource.deleteBook(bookId)
        bookDao.delete(bookId)
    }

    override suspend fun refreshBooks() {
        val numberOfBooks = bookDao.getBookCount()
        // TODO: implement proper syncing. Right now, the firestore is used as a simple online "back up" (which is
        //  downloaded only when there are no books in the app database)
        if (numberOfBooks == 0) {
            networkDataSource.getBooks().forEach { networkBook ->
                val bookEntity = fromNetworkBook(networkBook)
                bookDao.addOrUpdate(bookEntity)
                addBookToLibrary(bookEntity.id)

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
        return bookDao.getBooksForQuery(query).map(BookEntity::asExternalModel)
    }
}
