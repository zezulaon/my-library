package dev.zezula.books.tests.robot

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.tests.utils.DEFAULT_TIMEOUT

class FindBookRobot(val rule: AndroidComposeTestRule<*, *>) {

    fun typeSearchQuery(query: String) {
        rule.onNode(hasSetTextAction()).performTextInput(query)
    }

    fun submitSearch() {
        rule.onNode(hasSetTextAction()).performImeAction()
    }

    @OptIn(ExperimentalTestApi::class)
    fun assertSearchResultIsDisplayed(bookTitle: String) {
        rule.waitUntilAtLeastOneExists(
            hasText(bookTitle),
            DEFAULT_TIMEOUT,
        )
        rule.onNodeWithText(bookTitle).assertIsDisplayed()
    }

    fun tapOnSearchResult(bookTitle: String) {
        rule.onNodeWithText(bookTitle).performClick()
    }
}

fun AppRobot.onFindBookScreen(block: FindBookRobot.() -> Unit) {
    rule.verifyFindBookScreenDisplayed()
    FindBookRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyFindBookScreenDisplayed() {
    onNode(hasSetTextAction()).assertIsDisplayed()
}
