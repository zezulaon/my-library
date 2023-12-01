package dev.zezula.books.components

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import dev.zezula.books.R
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.model.review.previewRatings
import dev.zezula.books.data.model.review.previewReviews
import dev.zezula.books.data.model.shelf.previewShelvesForBook
import dev.zezula.books.ui.screen.detail.BookDetailScreen
import dev.zezula.books.ui.screen.detail.BookDetailUiState
import dev.zezula.books.ui.screen.detail.DetailTab
import org.junit.Rule
import org.junit.Test

class BookDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Check that detail tab is selected and the UI has correct book data displayed
     */
    @Test
    fun detail_check_correctDetailTabDisplayed() {
        val book = previewBooks.first()

        composeTestRule.apply {
            setContent {
                createTestScreen(
                    BookDetailUiState(
                        book = book,
                        rating = null,
                        shelves = emptyList(),
                        reviews = emptyList(),
                        selectedTab = DetailTab.Detail,
                        errorMessage = null,
                        isBookDeleted = false,
                        isReviewsSearchInProgress = false,
                    ),
                )
            }

            onNodeWithText(text = activity.getString(R.string.screen_detail_tab_book), useUnmergedTree = true)
                .assertIsDisplayed()
                .onParent()
                .assertIsSelected()

            onNodeWithText(book.title!!).assertIsDisplayed()
            onNodeWithText(book.author!!).assertIsDisplayed()
            onNodeWithText(book.pageCount?.toString()!!).assertIsDisplayed()
            onNodeWithText(book.publisher!!).assertIsDisplayed()
            onNodeWithText(book.yearPublished?.toString()!!).assertIsDisplayed()
            onNodeWithText(book.isbn!!).assertIsDisplayed()
            onNodeWithText(book.dateAddedFormatted).assertIsDisplayed()
        }
    }

    /**
     * Check that review tab is selected and the UI has correct reviews data displayed
     */
    @Test
    fun detail_check_correctReviewTabDisplayed() {
        val book = previewBooks.first()
        val rating = previewRatings.first()

        composeTestRule.apply {
            setContent {
                createTestScreen(
                    BookDetailUiState(
                        book = book,
                        rating = rating,
                        shelves = emptyList(),
                        reviews = previewReviews,
                        selectedTab = DetailTab.Reviews,
                        errorMessage = null,
                        isBookDeleted = false,
                        isReviewsSearchInProgress = false,
                    ),
                )
            }

            onNodeWithText(text = activity.getString(R.string.screen_detail_tab_reviews), useUnmergedTree = true)
                .assertIsDisplayed()
                .onParent()
                .assertIsSelected()

            onNodeWithText(book.title!!).assertIsDisplayed()
            onNodeWithText(book.author!!).assertIsDisplayed()
            onNodeWithText(rating.averageRating!!).assertIsDisplayed()
            onNodeWithText(previewReviews.first().userName!!).assertIsDisplayed()
            onNodeWithText(previewReviews.first().body!!).assertIsDisplayed()
        }
    }

    /**
     * Check that shelves tab is selected and the UI has correct shelves data displayed
     */
    @Test
    fun detail_check_correctShelvesTabDisplayed() {
        val book = previewBooks.first()

        composeTestRule.apply {
            setContent {
                createTestScreen(
                    BookDetailUiState(
                        book = book,
                        rating = null,
                        shelves = previewShelvesForBook,
                        reviews = emptyList(),
                        selectedTab = DetailTab.Shelves,
                        errorMessage = null,
                        isBookDeleted = false,
                        isReviewsSearchInProgress = false,
                        isBookInLibrary = true,
                    ),
                )
            }

            onNodeWithText(text = activity.getString(R.string.screen_detail_tab_shelves), useUnmergedTree = true)
                .assertIsDisplayed()
                .onParent()
                .assertIsSelected()

            onNodeWithText(book.title!!).assertIsDisplayed()
            onNodeWithText(book.author!!).assertIsDisplayed()
            onNodeWithText(previewShelvesForBook.first().title).assertIsDisplayed()
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun createTestScreen(state: BookDetailUiState) {
        BookDetailScreen(
            uiState = state,
            onNavigateBack = {},
            onEditBookClick = {},
            onNoteDeleteClick = {},
            onDeleteConfirmClick = {},
            onDeleteDialogDismissed = {},
            onNewShelfClick = {},
            onReviewClick = {},
            onDeleteClick = {},
            onShelfCheckedChange = { _, _ -> },
            onTabClick = {},
            onNewNoteClick = {},
            onNoteDialogDismissed = {},
            onNoteDialogSaveClick = { _ -> },
            onNoteDialogUpdateClick = { _, _ -> },
            onNoteEditClick = { _ -> },
        )
    }
}
