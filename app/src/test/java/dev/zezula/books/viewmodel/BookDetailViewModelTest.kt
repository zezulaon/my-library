package dev.zezula.books.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.zezula.books.MainDispatcherRule
import dev.zezula.books.TimberLogRule
import dev.zezula.books.data.model.book.previewBookEntities
import dev.zezula.books.data.model.review.ReviewEntity
import dev.zezula.books.data.model.shelf.previewShelfEntities
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ReviewDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.di.appUnitTestModule
import dev.zezula.books.ui.DestinationArgs
import dev.zezula.books.ui.screen.detail.BookDetailViewModel
import dev.zezula.books.ui.screen.detail.DetailTab
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
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
 *
 * Also Turbine was used in some of these tests:
 * https://github.com/cashapp/turbine
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest : KoinTest {

    private val viewModel: BookDetailViewModel by inject()
    private val shelfAndBookDao: ShelfAndBookDao by inject()
    private val bookDao: BookDao by inject()
    private val reviewDao: ReviewDao by inject()

    companion object {

        @get:ClassRule
        @JvmStatic // JvmStatic to satisfy ClassRule field requirements
        val timberLogRule = TimberLogRule()
    }

    // Rule that replaces main dispatcher used in ViewModel's scope with a test dispatcher used in these tests.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        val bookViewModuleOverride = module {
            viewModel {
                val savedStateHandle = SavedStateHandle()
                savedStateHandle[DestinationArgs.bookIdArg] = bookTestData.id
                BookDetailViewModel(
                    moveBookToLibraryUseCase = get(),
                    deleteBookUseCase = get(),
                    checkReviewsDownloadedUseCase = get(),
                    createOrUpdateNoteUseCase = get(),
                    deleteNoteUseCase = get(),
                    toggleBookInShelfUseCase = get(),
                    savedStateHandle = savedStateHandle,
                    getAllBookDetailUseCase = get(),
                )
            }
        }
        modules(appUnitTestModule, bookViewModuleOverride)
    }

    private val bookTestData = previewBookEntities.first()
    private val shelvesTestData = previewShelfEntities

    @Before
    fun setupRepository() = runTest {
        bookDao.addOrUpdate(bookTestData)
        shelfAndBookDao.addOrUpdate(shelvesTestData)
    }

    @Test
    fun book_detail_is_initialized() = runTest {
        viewModel.uiState.test {
            val uiState = awaitItem()
            // Check that book detail UI state has the correct book
            assertEquals(bookTestData.id, uiState.book?.id)
            // Check that the detail tab is selected
            assertEquals(DetailTab.Detail, uiState.selectedTab)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun tab_selection_selects_correct_tab() = runTest {
        viewModel.onTabClick(DetailTab.Shelves)
        viewModel.uiState.test {
            assertEquals(DetailTab.Shelves, awaitItem().selectedTab)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun after_delete_click_book_was_deleted() = runTest {
        // Check that there is a book before deleting it
        bookDao.getBookStream(bookTestData.id).test {
            assertEquals(bookTestData, awaitItem())
            cancelAndConsumeRemainingEvents()
        }

        viewModel.deleteBookConfirmed()
        // Check that the UI state is aware the book was deleted
        viewModel.uiState.test {
            assertTrue(awaitItem().isBookDeleted)
            cancelAndConsumeRemainingEvents()
        }

        // Check that the book was also removed from repository
        bookDao.getBookStream(bookTestData.id).test {
            assertNull(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun check_reviews_are_refreshed() = runTest {
        // Insert test review
        reviewDao.addReviews(listOf(ReviewEntity("123", bookTestData.id)))

        viewModel.uiState.test {
            val uiState = awaitItem()
            // Check UI state has some review data
            assertTrue(uiState.reviews.isNotEmpty())
            // Check that there is no progress for reviews fetch
            assertFalse(uiState.isInProgress)
            cancelAndConsumeRemainingEvents()
        }
    }
}
