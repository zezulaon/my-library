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
import dev.zezula.books.testtag.AllAuthorsTestTag

class AllAuthorsRobot(val rule: AndroidComposeTestRule<*, *>) {

    fun assertToolbarAuthorSize(size: Int) {
        val numberAuthorsFormatted = rule.activity.resources.getQuantityString(
            R.plurals.all_authors_number_of_authors_subtitle,
            size,
            size,
        )
        val hasToolbarAncestor = hasAnyAncestor(hasTestTag(AllAuthorsTestTag.CONTAINER_TOOLBAR))
        rule.onNode(hasText(numberAuthorsFormatted) and hasToolbarAncestor).assertIsDisplayed()
    }

    fun assertAuthorIsDisplayed(authorName: String) {
        rule.onAllNodesWithText(authorName).onFirst().assertIsDisplayed()
    }

    fun assertAuthorBookCountIsDisplayed(count: Int) {
        rule.onAllNodesWithText(count.toString()).onFirst().assertIsDisplayed()
    }

    fun tapOnAuthor(authorName: String) {
        rule.onAllNodesWithText(authorName).onFirst().performClick()
    }
}

fun AppRobot.onAllAuthorsScreen(block: AllAuthorsRobot.() -> Unit) {
    rule.verifyAllAuthorsScreenDisplayed()
    AllAuthorsRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyAllAuthorsScreenDisplayed() {
    onNodeWithTag(AllAuthorsTestTag.ROOT).assertIsDisplayed()
}
