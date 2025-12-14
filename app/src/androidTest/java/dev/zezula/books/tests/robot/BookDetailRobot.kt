package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zezula.books.R
import dev.zezula.books.core.model.Book
import dev.zezula.books.tests.utils.onNodeWithTextStringRes
import dev.zezula.books.testtag.BookDetailTestTag

class BookDetailRobot(private val rule: AndroidComposeTestRule<*, *>) {

    fun assertBookDisplayed(book: Book) {
        with(rule) {
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
