package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.R
import dev.zezula.books.tests.utils.onNodeWithTextStringRes
import dev.zezula.books.testtag.BookEditorTestTag

class BookEditorRobot(private val rule: AndroidComposeTestRule<*, *>) {

    fun typeInput(text: String, type: InputType, clearExistingText: Boolean = false) {
        val tag = when (type) {
            InputType.TITLE -> BookEditorTestTag.INPUT_TITLE
            InputType.AUTHOR -> BookEditorTestTag.INPUT_AUTHOR
            InputType.PUBLISHER -> BookEditorTestTag.INPUT_PUBLISHER
            InputType.YEAR -> BookEditorTestTag.INPUT_YEAR
            InputType.PAGES -> BookEditorTestTag.INPUT_PAGES
            InputType.ISBN -> BookEditorTestTag.INPUT_ISBN
            InputType.DESC -> BookEditorTestTag.INPUT_DESC
        }
        rule.onNodeWithTag(tag).apply {
            performClick()
            if (clearExistingText) {
                performTextClearance()
            }
            performTextInput(text)
        }
    }

    fun tapOnSave() {
        rule.onNodeWithTag(BookEditorTestTag.BTN_SAVE).performClick()
    }

    fun assertInvalidInputErrorDisplayed() {
        rule.onNodeWithTextStringRes(R.string.invalid_input_form)
            .assertIsDisplayed()
    }
}

fun AppRobot.onBookEditorScreen(block: BookEditorRobot.() -> Unit) {
    rule.verifyBookEditorScreenDisplayed()
    BookEditorRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyBookEditorScreenDisplayed() {
    onNodeWithTag(BookEditorTestTag.ROOT).assertIsDisplayed()
}

enum class InputType {
    TITLE,
    AUTHOR,
    PUBLISHER,
    YEAR,
    PAGES,
    ISBN,
    DESC,
}
