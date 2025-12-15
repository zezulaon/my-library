package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.R
import dev.zezula.books.tests.utils.onNodeWithTextStringRes
import dev.zezula.books.testtag.BookDetailTestTag
import dev.zezula.books.testtag.NotesTestTag

class NotesTabRobot(private val rule: AndroidComposeTestRule<*, *>) {

    fun addNote(text: String) {
        with(rule) {
            onNodeWithTag(BookDetailTestTag.BTN_ADD_NOTE)
                .performClick()

            onNodeWithTag(BookDetailTestTag.INPUT_NOTE_TEXT)
                .performTextInput(text)

            onNodeWithTextStringRes(R.string.detail_notes_dialog_btn_save)
                .performClick()
        }
    }

    fun updateNoteText(text: String) {
        rule.onNodeWithTag(BookDetailTestTag.INPUT_NOTE_TEXT).apply {
            performTextClearance()
            performTextInput(text)
        }
    }

    fun tapOnUpdateNote() {
        rule.onNodeWithTextStringRes(R.string.detail_notes_dialog_btn_update)
            .performClick()
    }

    fun tapOnDeleteNote(text: String) {
        expandNote(text)
        rule.onNodeWithTextStringRes(R.string.detail_notes_btn_remove)
            .performClick()
    }

    fun tapOnEditNote(text: String) {
        expandNote(text)
        rule.onNodeWithTextStringRes(R.string.detail_notes_btn_edit)
            .performClick()
    }

    private fun expandNote(text: String) {
        rule.onNode(hasTestTag(NotesTestTag.BTN_EXPAND_NOTE) and hasAnySibling(hasText(text)))
            .performClick()
    }

    fun assertNoteDisplayed(text: String) {
        rule.onNodeWithText(text).assertIsDisplayed()
    }

    fun assertNoteDoesNotExist(text: String) {
        rule.onNodeWithText(text).assertDoesNotExist()
    }
}

fun BookDetailRobot.onNotesTab(block: NotesTabRobot.() -> Unit) {
    rule.verifyNotesTabDisplayed()
    NotesTabRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyNotesTabDisplayed() {
    onNodeWithTag(BookDetailTestTag.TAB_CONTAINER_NOTES).assertIsDisplayed()
}
