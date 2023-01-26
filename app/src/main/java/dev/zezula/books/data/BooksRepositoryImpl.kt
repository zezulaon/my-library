package dev.zezula.books.data

import dev.zezula.books.data.model.FindBookOnlineResponse
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.fromNetworkBook
import dev.zezula.books.data.model.goodreads.toBookFormData
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.model.shelf.fromNetworkShelf
import dev.zezula.books.data.model.shelf.fromNetworkShelfWithBook
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class BooksRepositoryImpl(
    private val booksDao: BookDao,
    private val networkDataSource: NetworkDataSource,
    private val shelfAndBookDao: ShelfAndBookDao,
) : BooksRepository {

    override fun getBooksForShelfAsStream(shelfId: String): Flow<List<Book>> {
        return shelfAndBookDao.getBooksForShelfAsStream(shelfId).map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getAllBooksStream(): Flow<List<Book>> {
        return booksDao.getAllBooksAsStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getBookStream(bookId: String): Flow<Book?> {
        return booksDao.getBook(bookId)
            .map {
                it?.asExternalModel()
            }
    }

    override suspend fun getBook(bookId: String): Book? {
        return getBookStream(bookId).first()
    }

    override suspend fun addOrUpdateBook(bookId: String, bookFormData: BookFormData): Book {
        Timber.d("addOrUpdateBook($bookId, $bookFormData)")
        val networkBook = NetworkBook(
            id = bookId,
            title = bookFormData.title,
            author = bookFormData.author,
            isbn = bookFormData.isbn,
            description = bookFormData.description,
            dateAdded = LocalDateTime.now().toString(),
            publisher = bookFormData.publisher,
            thumbnailLink = bookFormData.thumbnailLink,
            yearPublished = bookFormData.yearPublished,
            pageCount = bookFormData.pageCount,
        )
        networkDataSource.addOrUpdateBook(networkBook)

        val bookEntity = fromNetworkBook(networkBook)
        booksDao.addOrUpdate(bookEntity)
        return bookEntity.asExternalModel()
    }

    override suspend fun addBook(bookFormData: BookFormData): Book {
        val createdId = UUID.randomUUID().toString()
        return addOrUpdateBook(createdId, bookFormData)
    }

    override suspend fun refreshBooks() {
        val numberOfBooks = booksDao.getBookCount()
        // TODO: implement proper syncing. Right now, the firestore is used as a simple online "back up" (which is
        //  downloaded only when there are no books in the app database)
        if (numberOfBooks == 0) {
            networkDataSource.getBooks().forEach { networkBook ->
                booksDao.addOrUpdate(fromNetworkBook(networkBook))
            }
            networkDataSource.getShelves().forEach { networkShelf ->
                shelfAndBookDao.addOrUpdate(fromNetworkShelf(networkShelf))
            }
            networkDataSource.getShelvesWithBooks().forEach { networkShelfWithBook ->
                shelfAndBookDao.addBookToShelf(fromNetworkShelfWithBook(networkShelfWithBook))
            }
        }
    }

    override suspend fun deleteBook(bookId: String) {
        networkDataSource.deleteBook(bookId)
        booksDao.delete(bookId)
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

    override suspend fun getBookId(isbn: String): String? {
        val dbBooks = booksDao.getForIsbn(isbn)
        return dbBooks.firstOrNull()?.id
    }

    override suspend fun addBook(fetchBookNetworkResponse: FindBookOnlineResponse): Book? {
        // Search in GoodReads online DB
        val goodReadsBook = fetchBookNetworkResponse.goodreadsBook
        val bookFormData = goodReadsBook?.toBookFormData()
        return if (bookFormData != null) {
            val addedBook = addBook(bookFormData)
            addedBook
        } else {
            null
        }
    }
}
