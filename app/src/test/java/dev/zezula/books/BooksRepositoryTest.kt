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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BooksRepositoryTest {

    private lateinit var booksRepository: BooksRepository
    private lateinit var booksDao: BookDao
    private lateinit var shelfAndBookDao: ShelfAndBookDao
    private lateinit var networkDataSource: NetworkDataSource

    @Before
    fun setup() {
        booksDao = FakeBookDaoImpl()
        shelfAndBookDao = FakeShelfAndBookDaoImpl()
        networkDataSource = FakeNetworkDataSourceImpl()
        booksRepository = BooksRepositoryImpl(
            booksDao = booksDao,
            networkDataSource = networkDataSource,
            shelfAndBookDao = shelfAndBookDao
        )
    }

    @Test
    fun booksRepository_stream_is_backed_by_book_dao() {
        runTest {
            assertEquals(
                booksDao.getAllBooksAsStream()
                    .first()
                    .map(BookEntity::asExternalModel),
                booksRepository.getAllBooksStream()
                    .first()
            )
        }
    }

    @Test
    fun booksRepository_book_is_added() {

        val bookFormData = BookFormData(title = "Title X", author = "Author X")

        runTest {
            booksRepository.addBook(bookFormData)

            assertTrue(
                booksDao.getAllBooksAsStream()
                    .first()
                    .any { entity ->
                        bookFormData.title == entity.title
                    }
            )

            assertTrue(
                networkDataSource.getBooks()
                    .any { book ->
                        bookFormData.title == book.title
                    }
            )

            // Check that created ID in dao and network are same
            assertEquals(
                booksDao.getAllBooksAsStream()
                    .first()
                    .map(BookEntity::asExternalModel)
                    .first { book -> book.title == bookFormData.title }.id,
                booksRepository.getAllBooksStream()
                    .first().first { book -> book.title == bookFormData.title }.id
            )
        }
    }

    @Test
    fun booksRepository_book_is_updated() {

        runTest {
            val bookToUpdate = booksRepository.getBook(previewBooks.first().id)!!
            val updatedTitle = "new title"
            booksRepository.addOrUpdateBook(bookId = bookToUpdate.id, bookFormData = BookFormData(title = updatedTitle))

            assertTrue(
                booksRepository.getBook(bookToUpdate.id)!!.title == updatedTitle
            )

            assertTrue(
                booksDao.getBook(bookToUpdate.id).first()!!.title == updatedTitle
            )

            assertTrue(
                networkDataSource.getBooks()
                    .any { book ->
                        book.title == updatedTitle
                    }
            )
        }
    }

    @Test
    fun booksRepository_delete_removes_books() {

        runTest {
            val bookToDelete = booksRepository.getBook(previewBooks.first().id)!!
            booksRepository.deleteBook(bookToDelete.id)

            assertNull(booksRepository.getBook(bookToDelete.id))
            assertNull(booksDao.getBook(bookToDelete.id).first())
            assertFalse(
                networkDataSource.getBooks()
                    .any { book ->
                        book.id == bookToDelete.id
                    }
            )
        }
    }

    @Test
    fun booksRepository_refresh_fetches_books_from_network() {

        runTest {
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
}