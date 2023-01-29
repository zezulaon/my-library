package dev.zezula.books.viewmodel

import app.cash.turbine.test
import dev.zezula.books.MainDispatcherRule
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.previewBookEntities
import dev.zezula.books.data.model.shelf.previewShelfEntities
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.data.source.network.fake.FakeNetworkDataSourceImpl
import dev.zezula.books.di.appUnitTestModule
import dev.zezula.books.ui.screen.list.BookListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * The [androidx.lifecycle.ViewModel] tests use Repositories/UseCases initialized with fake data sources (DAO, Network)
 * to test flow coming from the data layer. On the other hand, flow emissions (mostly UI state emissions) are tested via
 * [kotlinx.coroutines.flow.StateFlow]'s current value - tests validates the [androidx.lifecycle.ViewModel]'s latest
 * UI state.
 *
 * For the StateFlow object to work in these tests, there must be at least one collector for the given flow. Also all
 * coroutines that collect these flows have to be cancelled at the end of each test.
 *
 * More info:
 * https://developer.android.com/kotlin/flow/test#statein
 * https://developer.android.com/kotlin/coroutines/test
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BookListViewModelTest : KoinTest {

    private val viewModel: BookListViewModel by inject()
    private val shelfAndBookDao: ShelfAndBookDao by inject()
    private val bookDao: BookDao by inject()

    // Rule that replaces main dispatcher used in ViewModel's scope with a test dispatcher used in these tests.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appUnitTestModule)
    }

    private val booksTestData = listOf(previewBookEntities.first())
    private val shelvesTestData = previewShelfEntities

    @Before
    fun setupRepository() = runTest {
        bookDao.addOrUpdate(booksTestData)
        shelfAndBookDao.addOrUpdate(shelvesTestData)
    }

    @Test
    fun error_message_is_displayed_after_refresh_fails_with_ioException() = runTest {
        // Override module with custom data source that throws exception for GET call
        loadKoinModules(
            module {
                single<NetworkDataSource> {
                    object : FakeNetworkDataSourceImpl() {
                        override suspend fun getBooks(): List<NetworkBook> {
                            throw IOException("Failed to fetch books")
                        }
                    }
                }
            },
        )

        // Refresh is only launched when DB is empty
        bookDao.deleteAll()
        // Refresh DB from network source
        viewModel.refresh()

        viewModel.uiState.test {
            val uiState = awaitItem()
            // Check that there is error message in UI state after the exception was thrown
            assertNotNull(uiState.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun shelves_are_initialized() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        assertEquals(shelvesTestData.size, viewModel.uiState.value.shelves.size)
        collectJob.cancel()
    }

    @Test
    fun shelf_is_selected_after_navigation_drawer_shelf_is_clicked() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val shelfToSelect = viewModel.uiState.value.shelves.last()

        // Check that no shelf is selected (default)
        assertNull(viewModel.uiState.value.selectedShelf)

        viewModel.onShelfSelected(shelfToSelect)
        // Check that shelf is selected
        assertEquals(shelfToSelect, viewModel.uiState.value.selectedShelf)

        collectJob.cancel()
    }

    @Test
    fun all_books_are_displayed_after_all_books_shelf_item_is_clicked() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        viewModel.onAllBooksShelfSelected()
        with(viewModel.uiState.value) {
            // Check that "all books" (no shelf) is selected
            assertEquals(null, selectedShelf)
            // Check that all books are displayed
            assertEquals(booksTestData.size, books.size)
        }

        collectJob.cancel()
    }
}
