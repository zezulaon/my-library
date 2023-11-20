package dev.zezula.books.repository

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.previewBookEntities
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.di.appUnitTestModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BooksRepositoryTest : KoinTest {

    private val booksRepository: BooksRepository by inject()
    private val bookDao: BookDao by inject()
//    private val networkDataSource: NetworkDataSource by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appUnitTestModule)
    }

    private val booksTestData = listOf(previewBookEntities.first())

    @Before
    fun setupRepository() = runTest {
        bookDao.addOrUpdate(booksTestData)
    }

    @Test
    fun books_stream_is_backed_by_book_dao() = runTest {
        assertEquals(
            bookDao.getAllBooksStream()
                .first()
                .map(BookEntity::asExternalModel),
            booksRepository.getAllBooksStream()
                .first(),
        )
    }

    @Test
    fun book_is_added_and_backed_by_dao_and_network() = runTest {
        val bookFormData = BookFormData(title = "Title X", author = "Author X")
        booksRepository.addBook(bookFormData)

        // Check that book was added to DB
        assertTrue(
            bookDao.getAllBooksStream()
                .first()
                .any { entity ->
                    bookFormData.title == entity.title
                },
        )

        // Check that repository returns same IDs as DB
        assertEquals(
            bookDao.getAllBooksStream()
                .first()
                .map(BookEntity::asExternalModel)
                .first { book -> book.title == bookFormData.title }.id,
            booksRepository.getAllBooksStream()
                .first().first { book -> book.title == bookFormData.title }.id,
        )
    }

    @Test
    fun book_is_updated() = runTest {
        val bookToUpdate = booksRepository.getBook(booksTestData.first().id)!!
        val updatedTitle = "new title"
        booksRepository.updateBook(
            bookId = bookToUpdate.id,
            bookFormData = BookFormData(title = updatedTitle, dateAdded = "2022-01-05T17:43:25.629"),
        )

        // Check that the book in the repository was updated
        assertEquals(updatedTitle, booksRepository.getBook(bookToUpdate.id)!!.title)
        // Check that the book in DB was updated
        assertEquals(updatedTitle, bookDao.getBookStream(bookToUpdate.id).first()!!.title)
    }

    @Test
    fun delete_removes_the_book() = runTest {
        val bookToDelete = booksRepository.getBook(booksTestData.first().id)!!
        booksRepository.deleteBook(bookToDelete.id)

        // Check that the book was deleted from all data sources
        assertNull(booksRepository.getBook(bookToDelete.id))
        assertNull(bookDao.getBookStream(bookToDelete.id).first())
    }
}
