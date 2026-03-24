package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.zezula.books.R
import dev.zezula.books.tests.utils.TestBook
import dev.zezula.books.tests.utils.onNodeWithTextStringRes
import dev.zezula.books.testtag.BookDetailTestTag

class BookDetailRobot(val rule: AndroidComposeTestRule<*, *>) {

    fun assertBookDisplayed(book: TestBook) {
        with(rule) {
            onNodeWithText(book.title).assertIsDisplayed()
            onNodeWithText(book.author).assertIsDisplayed()
            onNodeWithText(book.publisher).assertIsDisplayed()
            onNodeWithText(book.yearPublished.toString()).assertIsDisplayed()
            onNodeWithText(book.pageCount.toString()).assertIsDisplayed()
            onNodeWithText(book.isbn).assertIsDisplayed()
            onNodeWithText(book.description).assertIsDisplayed()
        }
    }

    fun tapOnDeleteButton() {
        rule.onNodeWithTag(BookDetailTestTag.BTN_DELETE_BOOK)
            .performClick()
    }

    fun tapOnEditButton() {
        rule.onNodeWithTag(BookDetailTestTag.BTN_EDIT_BOOK)
            .performClick()
    }

    fun confirmDeletion() {
        rule.onNodeWithTextStringRes(R.string.detail_btn_confirm_delete)
            .performClick()
    }

    fun tapOnTab(tab: BookDetailTab) {
        val tabStringRes = when (tab) {
            BookDetailTab.SHELVES -> R.string.screen_detail_tab_shelves
            BookDetailTab.DETAIL -> R.string.screen_detail_tab_book
            BookDetailTab.REVIEWS -> R.string.screen_detail_tab_reviews
            BookDetailTab.SUGGESTIONS -> R.string.screen_detail_tab_suggestions
            BookDetailTab.NOTES -> R.string.screen_detail_tab_notes
        }
        with(rule) {
            val tabLabel = activity.getString(tabStringRes)
            onNode(hasText(tabLabel) and hasAnyAncestor(hasTestTag(BookDetailTestTag.CONTAINER_TAB_BAR)))
                .performScrollTo()
                .performClick()
        }
    }

    fun toggleShelfSelection(shelfTitle: String) {
        rule.onNodeWithTag(BookDetailTestTag.checkboxShelf(shelfTitle))
            .performClick()
    }

    fun assertShelfDoesNotExist(shelfTitle: String) {
        rule.onNodeWithTag(BookDetailTestTag.checkboxShelf(shelfTitle))
            .assertDoesNotExist()
    }

    fun tapOnManageShelvesButton() {
        rule.onNodeWithTag(BookDetailTestTag.BTN_MANAGE_SHELVES)
            .performClick()
    }
}

fun AppRobot.onBookDetailScreen(block: BookDetailRobot.() -> Unit) {
    rule.verifyBookDetailScreenDisplayed()
    BookDetailRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyBookDetailScreenDisplayed() {
    onNodeWithTag(BookDetailTestTag.ROOT).assertIsDisplayed()
}

enum class BookDetailTab {
    SHELVES,
    DETAIL,
    REVIEWS,
    SUGGESTIONS,
    NOTES,
}
