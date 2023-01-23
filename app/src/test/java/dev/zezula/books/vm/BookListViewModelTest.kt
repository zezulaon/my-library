package dev.zezula.books.vm

import dev.zezula.books.MainDispatcherRule
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.model.shelf.previewShelves
import dev.zezula.books.di.appUnitTestModule
import dev.zezula.books.ui.screen.list.BookListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BookListViewModelTest: KoinTest {

    private val viewModel: BookListViewModel by inject()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appUnitTestModule)
    }

    @Test
    fun shelves_are_initialized() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        assertEquals(previewShelves, viewModel.uiState.value.shelves)
        collectJob.cancel()
    }

    @Test
    fun shelf_is_selected_after_navigation_drawer_shelf_is_clicked() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val shelfToSelect = previewShelves.first()
        viewModel.onShelfSelected(shelfToSelect)
        val uiState = viewModel.uiState.value
        // Check that shelf is selected
        assertEquals(shelfToSelect, uiState.selectedShelf)
        // Check that shelf has correct number of books
        assertEquals(shelfToSelect.numberOfBooks, uiState.books.size)

        collectJob.cancel()
    }

    @Test
    fun all_books_are_displayed_after_all_books_shelf_item_is_clicked() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        viewModel.onAllBooksShelfSelected()
        val uiState = viewModel.uiState.value
        // Check that "all books" (no shelf) is selected
        assertEquals(null, uiState.selectedShelf)
        // Check that all books are displayed
        assertEquals(previewBooks.size, uiState.books.size)

        collectJob.cancel()
    }
}