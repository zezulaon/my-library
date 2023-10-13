package dev.zezula.books.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSiblings
import dev.zezula.books.R
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.model.shelf.previewShelves
import dev.zezula.books.ui.screen.list.BookListScreen
import dev.zezula.books.ui.screen.list.BookListUiState
import dev.zezula.books.ui.screen.list.DrawerNavigationState
import dev.zezula.books.ui.screen.signin.SignInUiState
import dev.zezula.books.util.homeAppBar
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalMaterial3Api::class)
    @Before
    fun setContent() {
        composeTestRule.setContent {
            BookListScreen(
                uiState = BookListUiState(
                    books = previewBooks,
                    drawerNavigation = DrawerNavigationState(shelves = previewShelves, managedShelvesClicked = true),
                ),
                signUiState = SignInUiState()
            )
        }
    }

    @Test
    fun bookList_isDisplayed() {
        composeTestRule.apply {
            // App name exists (in navigation drawer) but is not displayed (nav drawer starts in closed state)
            onNodeWithText(text = activity.getString(R.string.app_name), ignoreCase = true).assertExists()
            onNodeWithText(text = activity.getString(R.string.app_name), ignoreCase = true).assertIsNotDisplayed()

            // "All books" shelf is displayed
            onNodeWithTag(homeAppBar)
                .onChildren()
                .assertAny(hasText(activity.getString(R.string.home_shelf_title_all_books)))

            // Book with specific title is displayed
            onNodeWithText(previewBooks[0].title!!).assertIsDisplayed()
        }
    }

    @Test
    fun bookList_bottomBarHasButtons() {
        composeTestRule.apply {
            onNodeWithContentDescription(activity.getString(R.string.content_open_drawer))
                .assertIsDisplayed()
            onNodeWithText(text = activity.getString(R.string.home_btn_add), useUnmergedTree = true)
                .assertIsDisplayed()
        }
    }

    @Test
    fun bookList_hasShelfWithBookCount() {
        composeTestRule.onNodeWithText(previewShelves[0].title, useUnmergedTree = true)
            .assertExists()
            .onSiblings()
            .assertAny(hasText(previewShelves[0].numberOfBooks.toString()))
    }
}
