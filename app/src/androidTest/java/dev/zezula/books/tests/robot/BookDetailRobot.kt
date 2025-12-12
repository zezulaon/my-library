package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zezula.books.R
import dev.zezula.books.core.model.Book
import dev.zezula.books.tests.utils.onNodeWithTextStringRes
import dev.zezula.books.testtag.BookDetailTestTag

class BookDetailRobot {

    fun ComposeTestRule.assertBookDisplayed(book: Book) {
        with(book) {
            title?.let {
                onNodeWithText(it).assertIsDisplayed()
            }
            author?.let {
                onNodeWithText(it).assertIsDisplayed()
            }
            publisher?.let {
                onNodeWithText(it).assertIsDisplayed()
            }
            yearPublished?.let {
                onNodeWithText(it.toString()).assertIsDisplayed()
            }
            pageCount?.let {
                onNodeWithText(it.toString()).assertIsDisplayed()
            }
            isbn?.let {
                onNodeWithText(it).assertIsDisplayed()
            }
            description?.let {
                onNodeWithText(it).assertIsDisplayed()
            }
        }
    }

    fun ComposeTestRule.tapOnDeleteButton() {
        onNodeWithTag(BookDetailTestTag.BTN_DELETE_BOOK)
            .performClick()
    }

    fun ComposeTestRule.tapOnEditButton() {
        onNodeWithTag(BookDetailTestTag.BTN_EDIT_BOOK)
            .performClick()
    }

    fun AndroidComposeTestRule<*, *>.confirmDeletion() {
        onNodeWithTextStringRes(R.string.detail_btn_confirm_delete)
            .performClick()
    }

    fun AndroidComposeTestRule<*, *>.tapOnNavigateUp() {
        onNodeWithContentDescription(activity.getString(R.string.content_desc_navigate_back))
            .performClick()
    }

    fun AndroidComposeTestRule<*, *>.tapOnTab(tab: BookDetailTab) {
        val tabStringRes = when (tab) {
            BookDetailTab.SHELVES -> R.string.screen_detail_tab_shelves
            BookDetailTab.DETAIL -> R.string.screen_detail_tab_book
            BookDetailTab.REVIEWS -> R.string.screen_detail_tab_reviews
            BookDetailTab.SUGGESTIONS -> R.string.screen_detail_tab_suggestions
            BookDetailTab.NOTES -> R.string.screen_detail_tab_notes
        }
        val tabLabel = activity.getString(tabStringRes)

        onNode(hasText(tabLabel) and hasAnyAncestor(hasTestTag(BookDetailTestTag.CONTAINER_TAB_BAR)))
            .performClick()
    }

    fun ComposeTestRule.toggleShelfSelection(shelfTitle: String) {
        onNodeWithTag(BookDetailTestTag.checkboxShelf(shelfTitle)).apply {
            assertIsOff()
            performClick()
            assertIsOn()
        }
    }
}

fun ComposeTestRule.onBookDetailScreen(scope: BookDetailRobot.() -> Unit) {
    verifyBookDetailScreenDisplayed()
    BookDetailRobot().apply(scope)
}

private fun ComposeTestRule.verifyBookDetailScreenDisplayed() {
    onNodeWithTag(BookDetailTestTag.ROOT).assertIsDisplayed()
}

enum class BookDetailTab {
    SHELVES,
    DETAIL,
    REVIEWS,
    SUGGESTIONS,
    NOTES,
}