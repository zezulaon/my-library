package dev.zezula.books.components

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.zezula.books.R
import dev.zezula.books.ui.screen.search.SearchBarcodeScreen
import dev.zezula.books.ui.screen.search.SearchBarcodeUiState
import org.junit.Rule
import org.junit.Test

class SearchBookScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Check that when no book is found, UI has correct data displayed
     */
    @Test
    fun searchBook_check_that_correctNotFoundInfoIsDisplayed() {
        composeTestRule.apply {
            setContent {
                createTestScreen(
                    SearchBarcodeUiState(
                        barcode = "123",
                        isInProgress = true,
                        noBookFound = true,
                        errorMessage = null,
                        foundBookId = null,
                    ),
                )
            }

            onNodeWithText(text = activity.getString(R.string.search_no_book_found_for_isbn)).assertIsDisplayed()
            onNodeWithText(text = activity.getString(R.string.search_btn_scan_again)).assertIsDisplayed()
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun createTestScreen(state: SearchBarcodeUiState) {
        SearchBarcodeScreen(
            uiState = state,
            onNavigateBack = {},
            onScanAgainClick = {},
        )
    }
}
