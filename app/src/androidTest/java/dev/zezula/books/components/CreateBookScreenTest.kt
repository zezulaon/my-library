package dev.zezula.books.components

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import dev.zezula.books.R
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.ui.screen.create.CreateBookScreen
import dev.zezula.books.ui.screen.create.CreateBookUiState
import org.junit.Rule
import org.junit.Test

class CreateBookScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Check that AppBar has correct title/button in save mode.
     */
    @Test
    fun createBook_check_correctSaveAppBar() {

        composeTestRule.apply {
            setContent {
                createTestScreen(
                    CreateBookUiState(
                        bookFormData = BookFormData(),
                        isInEditMode = false,
                        isInProgress = false,
                        isBookSaved = false,
                        errorMessage = null,
                        invalidForm = false
                    )
                )
            }

            onNodeWithText(text = activity.getString(R.string.screen_title_create_new_book), useUnmergedTree = true)
                .assertIsDisplayed()

            onNodeWithText(text = activity.getString(R.string.btn_add), useUnmergedTree = true)
                .assertIsDisplayed()
                .assertIsEnabled()
        }
    }

    /**
     * Check that AppBar has correct title/button in edit mode.
     */
    @Test
    fun createBook_check_correctEditAppBar() {

        composeTestRule.apply {
            composeTestRule.setContent {
                createTestScreen(
                    CreateBookUiState(
                        bookFormData = BookFormData(),
                        isInEditMode = true,
                        isInProgress = false,
                        isBookSaved = false,
                        errorMessage = null,
                        invalidForm = false
                    )
                )
            }

            onNodeWithText(text = activity.getString(R.string.screen_title_update_book), useUnmergedTree = true)
                .assertIsDisplayed()

            onNodeWithText(text = activity.getString(R.string.btn_update), useUnmergedTree = true)
                .assertIsDisplayed()
                .assertIsEnabled()
        }

    }

    /**
     * Check that the UI is disabled during progress.
     */
    @Test
    fun createBook_check_correctInProgressState() {

        composeTestRule.apply {
            setContent {
                createTestScreen(
                    CreateBookUiState(
                        bookFormData = BookFormData(),
                        isInEditMode = true,
                        isInProgress = true,
                        isBookSaved = false,
                        errorMessage = null,
                        invalidForm = false
                    )
                )
            }

            onNodeWithText(text = activity.getString(R.string.btn_update), useUnmergedTree = true)
                .assertIsDisplayed()
                .onParent()
                .assertIsNotEnabled()

            onAllNodes(isNotEnabled())
                .assertCountEquals(8) // ALl 8 UI interactable components are disabled
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun createTestScreen(state: CreateBookUiState) {
        CreateBookScreen(
            uiState = state,
            onNavigateBack = {},
            onSaveButtonClick = {},
            onTitleValueChanged = {},
            onDescValueChanged = {},
            onIsbnValueChanged = {},
            onAuthorValueChanged = {},
            onPublisherValueChanged = {},
            onYearPublishedValueChanged = {},
            onPageCountValueChanged = {}
        )
    }
}