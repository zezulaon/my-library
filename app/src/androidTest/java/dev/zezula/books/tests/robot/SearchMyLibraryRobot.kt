package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.testtag.SearchMyLibraryTestTag

class SearchMyLibraryRobot(val rule: AndroidComposeTestRule<*, *>) {

    fun typeSearchQuery(query: String) {
        rule.onNode(hasSetTextAction()).performTextInput(query)
    }

    fun assertSearchResultIsDisplayed(bookTitle: String) {
        rule.onNodeWithText(bookTitle).assertIsDisplayed()
    }

    fun assertSearchResultDoesNotExist(bookTitle: String) {
        rule.onNodeWithText(bookTitle).assertDoesNotExist()
    }

    fun tapOnSearchResult(bookTitle: String) {
        rule.onNodeWithText(bookTitle).performClick()
    }
}

fun AppRobot.onSearchMyLibraryScreen(block: SearchMyLibraryRobot.() -> Unit) {
    rule.verifySearchMyLibraryScreenDisplayed()
    SearchMyLibraryRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifySearchMyLibraryScreenDisplayed() {
    onNodeWithTag(SearchMyLibraryTestTag.INPUT_QUERY).assertIsDisplayed()
}
