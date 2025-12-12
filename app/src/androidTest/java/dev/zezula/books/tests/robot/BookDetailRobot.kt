package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
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
}

fun ComposeTestRule.onBookDetailScreen(scope: BookDetailRobot.() -> Unit) {
    verifyBookDetailScreenDisplayed()
    BookDetailRobot().apply(scope)
}

private fun ComposeTestRule.verifyBookDetailScreenDisplayed() {
    onNodeWithTag(BookDetailTestTag.ROOT).assertIsDisplayed()
}
