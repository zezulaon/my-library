package dev.zezula.books

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.BooksRepositoryImpl
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.fake.FakeBookDaoImpl
import dev.zezula.books.data.source.db.fake.FakeShelfAndBookDaoImpl
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.data.source.network.fake.FakeNetworkDataSourceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

@OptIn(ExperimentalCoroutinesApi::class)
class BooksRepositoryTest {

    private lateinit var booksRepository: BooksRepository
    private lateinit var booksDao: BookDao
    private lateinit var shelfAndBookDao: ShelfAndBookDao
    private lateinit var networkDataSource: NetworkDataSource

    @Before
    fun setup() {
        startKoin {
            modules()
        }
        booksDao = FakeBookDaoImpl()
        shelfAndBookDao = FakeShelfAndBookDaoImpl()
        networkDataSource = FakeNetworkDataSourceImpl()
        booksRepository = BooksRepositoryImpl(
            booksDao = booksDao,
            networkDataSource = networkDataSource,
            shelfAndBookDao = shelfAndBookDao
        )
    }

    @After
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun books_stream_is_backed_by_book_dao() = runTest {
        assertEquals(
            booksDao.getAllBooksAsStream()
                .first()
                .map(BookEntity::asExternalModel),
            booksRepository.getAllBooksStream()
                .first()
        )
    }

    @Test
    fun book_is_added_and_backed_by_dao_and_network() = runTest {
        val bookFormData = BookFormData(title = "Title X", author = "Author X")
        booksRepository.addBook(bookFormData)

        // Check that book was added to DB
        assertTrue(
            booksDao.getAllBooksAsStream()
                .first()
                .any { entity ->
                    bookFormData.title == entity.title
                }
        )

        // Check that the book was added to network data source
        assertTrue(
            networkDataSource.getBooks()
                .any { book ->
                    bookFormData.title == book.title
                }
        )

        // Check that repository returns same IDs as DB
        assertEquals(
            booksDao.getAllBooksAsStream()
                .first()
                .map(BookEntity::asExternalModel)
                .first { book -> book.title == bookFormData.title }.id,
            booksRepository.getAllBooksStream()
                .first().first { book -> book.title == bookFormData.title }.id
        )
    }

    @Test
    fun book_is_updated() = runTest {
        val bookToUpdate = booksRepository.getBook(previewBooks.first().id)!!
        val updatedTitle = "new title"
        booksRepository.addOrUpdateBook(bookId = bookToUpdate.id, bookFormData = BookFormData(title = updatedTitle))

        // Check that the book in the repository was updated
        assertEquals(updatedTitle, booksRepository.getBook(bookToUpdate.id)!!.title)
        // Check that the book in DB was updated
        assertEquals(updatedTitle, booksDao.getBook(bookToUpdate.id).first()!!.title)
        // Check that the book in network data source was updated
        assertTrue(
            networkDataSource.getBooks()
                .any { book ->
                    book.title == updatedTitle
                }
        )
    }

    @Test
    fun delete_removes_the_book() = runTest {
        val bookToDelete = booksRepository.getBook(previewBooks.first().id)!!
        booksRepository.deleteBook(bookToDelete.id)

        // Check that the book was deleted from all data sources
        assertNull(booksRepository.getBook(bookToDelete.id))
        assertNull(booksDao.getBook(bookToDelete.id).first())
        assertFalse(
            networkDataSource.getBooks()
                .any { book ->
                    book.id == bookToDelete.id
                }
        )
    }

    @Test
    fun refresh_fetches_books_from_network_and_saves_them_to_database() = runTest {
        // delete local DB
        booksDao.deleteAll()
        // Check that repo is empty after delete
        assertTrue(booksRepository.getAllBooksStream().first().isEmpty())

        booksRepository.refreshBooks()
        // Check that repo is not empty
        assertFalse(booksRepository.getAllBooksStream().first().isEmpty())

        // Check that repo and network are same
        assertEquals(
            networkDataSource.getBooks()
                .map { it.id },
            booksRepository.getAllBooksStream()
                .first().map { it.id }
        )
    }
}