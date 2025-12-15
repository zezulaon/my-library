package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zezula.books.R
import dev.zezula.books.testtag.AllNotesTestTag

class AllNotesRobot(val rule: AndroidComposeTestRule<*, *>) {

    fun assertNoteDisplayed(text: String, bookTitle: String) {
        rule.onNode(
            hasTestTag(AllNotesTestTag.CONTAINER_NOTE_ITEM) and
                hasAnyDescendant(hasText(text)) and
                hasAnyDescendant(hasText(bookTitle)),
            useUnmergedTree = true,
        )
            .assertIsDisplayed()
    }

    fun assertNoteDoesNotExist(text: String) {
        rule.onNodeWithText(text).assertDoesNotExist()
    }

    fun assertNumberOfNotes(count: Int) {
        with(rule) {
            val countString = activity.resources.getQuantityString(
                R.plurals.all_notes_number_of_notes_subtitle,
                count,
                count,
            )
            onNodeWithText(countString).assertIsDisplayed()
        }
    }

    fun tapOnNote(text: String) {
        rule.onNodeWithText(text).performClick()
    }
}

fun AppRobot.onAllNotesScreen(block: AllNotesRobot.() -> Unit) {
    rule.verifyAllNotesScreenDisplayed()
    AllNotesRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyAllNotesScreenDisplayed() {
    onNodeWithTag(AllNotesTestTag.ROOT).assertIsDisplayed()
}
