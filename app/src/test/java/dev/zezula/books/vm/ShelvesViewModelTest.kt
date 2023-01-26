package dev.zezula.books.vm

import dev.zezula.books.MainDispatcherRule
import dev.zezula.books.data.model.shelf.previewShelfEntities
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.di.appUnitTestModule
import dev.zezula.books.ui.screen.shelves.ShelvesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
class ShelvesViewModelTest : KoinTest {

    private val viewModel: ShelvesViewModel by inject()
    private val shelfAndBookDao: ShelfAndBookDao by inject()

    // Rule that replaces main dispatcher used in ViewModel's scope with a test dispatcher used in these tests.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appUnitTestModule)
    }

    private val shelvesTestData = previewShelfEntities

    @Before
    fun setupRepository() = runTest {
        shelfAndBookDao.addOrUpdate(shelvesTestData)
    }

    @Test
    fun shelves_are_initialized() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        // Check that UI state in viewModel has same amount of shelves as test data
        assertEquals(shelvesTestData.size, viewModel.uiState.value.shelves.size)
        collectJob.cancel()
    }

    @Test
    fun new_shelf_can_be_created() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val createdShelfTitle = "test shelf title"
        viewModel.createShelf(createdShelfTitle)

        // Check that the UI state was updated with the new shelf
        assertTrue(viewModel.uiState.value.shelves.any { it.title == createdShelfTitle })

        collectJob.cancel()
    }

    @Test
    fun shelf_can_be_updated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val shelfToUpdate = viewModel.uiState.value.shelves.first()

        val updatedShelfTitle = "test update shelf title"
        viewModel.updateShelf(shelfToUpdate, updatedShelfTitle)

        // Check that the UI state was updated with the new shelf
        assertTrue(viewModel.uiState.value.shelves.any { it.title == updatedShelfTitle })

        collectJob.cancel()
    }

    @Test
    fun shelf_can_be_deleted() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val shelfToDelete = viewModel.uiState.value.shelves.first()

        viewModel.deleteShelf(shelfToDelete)

        // Check that the UI state was updated with the new shelf
        assertFalse(viewModel.uiState.value.shelves.contains(shelfToDelete))

        collectJob.cancel()
    }

    @Test
    fun on_edit_button_click_shows_dialog() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Check initial state -> no shelf is selected and no dialog is being shown
        assertNull(viewModel.uiState.value.selectedShelf)
        assertFalse(viewModel.uiState.value.showAddOrEditShelfDialog)

        val shelfToEdit = viewModel.uiState.value.shelves.first()
        viewModel.onEditShelfClicked(shelfToEdit)

        // Check that correct shelf is selected and the dialog is being shown
        assertEquals(shelfToEdit, viewModel.uiState.value.selectedShelf)
        assertTrue(viewModel.uiState.value.showAddOrEditShelfDialog)

        collectJob.cancel()
    }
}
