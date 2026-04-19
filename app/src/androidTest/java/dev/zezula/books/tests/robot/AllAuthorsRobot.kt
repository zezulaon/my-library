package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import dev.zezula.books.testtag.AllNotesTestTag

class AllAuthorsRobot(val rule: AndroidComposeTestRule<*, *>)

fun AppRobot.onAllAuthorsScreen(block: AllAuthorsRobot.() -> Unit) {
    rule.verifyAllAuthorsScreenDisplayed()
    AllAuthorsRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyAllAuthorsScreenDisplayed() {
    onNodeWithTag(AllNotesTestTag.ROOT).assertIsDisplayed()
}
