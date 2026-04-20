package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.zezula.books.R
import dev.zezula.books.testtag.AuthorBooksTestTag

class AuthorBooksRobot(val rule: AndroidComposeTestRule<*, *>) {

    fun assertToolbarAuthorName(authorName: String) {
        val hasToolbarAncestor = hasAnyAncestor(hasTestTag(AuthorBooksTestTag.CONTAINER_TOOLBAR))
        rule.onNode(hasText(authorName) and hasToolbarAncestor).assertIsDisplayed()
    }

    fun assertToolbarBookSize(size: Int) {
        val numberBooksFormatted = rule.activity.resources.getQuantityString(
            R.plurals.home_number_of_books_subtitle,
            size,
            size,
        )
        val hasToolbarAncestor = hasAnyAncestor(hasTestTag(AuthorBooksTestTag.CONTAINER_TOOLBAR))
        rule.onNode(hasText(numberBooksFormatted) and hasToolbarAncestor).assertIsDisplayed()
    }

    fun tapOnBookTitle(bookTitle: String) {
        rule.onAllNodesWithText(bookTitle).onFirst().performClick()
    }
}

fun AppRobot.onAuthorBooksScreen(block: AuthorBooksRobot.() -> Unit) {
    rule.verifyAuthorBooksScreenDisplayed()
    AuthorBooksRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyAuthorBooksScreenDisplayed() {
    onNodeWithTag(AuthorBooksTestTag.ROOT).assertIsDisplayed()
}
