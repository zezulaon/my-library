package dev.zezula.books.repository

import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.data.model.book.previewBookEntities
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.network.NetworkDataSource
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserLibraryRepositoryTest : KoinTest {

    private val userLibraryRepository: UserLibraryRepository by inject()
    private val bookDao: BookDao by inject()
    private val networkDataSource: NetworkDataSource by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appUnitTestModule)
    }

    private val booksTestData = listOf(previewBookEntities.first())

    @Before
    fun setupRepository() = runTest {
        bookDao.addOrUpdate(booksTestData)
    }

//    @Test
//    fun refresh_fetches_books_from_network_and_saves_them_to_database() = runTest {
//        // delete local DB
//        bookDao.deleteAll()
//        // Check that repo is empty after delete
//        assertTrue(userLibraryRepository.getAllLibraryBooksStream().first().isEmpty())
//
//        userLibraryRepository.refreshBooks()
//        // Check that repo is not empty
//        assertFalse(userLibraryRepository.getAllLibraryBooksStream().first().isEmpty())
//
//        // Check that repo and network are same
//        // FIXME: test
////        assertEquals(
////            networkDataSource.getBooks()
////                .map { it.id },
////            userLibraryRepository.getAllLibraryBooksStream()
////                .first().map { it.id },
////        )
//    }
}
