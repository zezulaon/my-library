package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.R
import dev.zezula.books.tests.onNodeWithStringRes
import dev.zezula.books.testtag.BookEditorTestTag

class BookEditorRobot {

    fun ComposeTestRule.typeInput(content: String, type: InputType) {
        val tag = when (type) {
            InputType.TITLE -> BookEditorTestTag.INPUT_TITLE
            InputType.AUTHOR -> BookEditorTestTag.INPUT_AUTHOR
            InputType.PUBLISHER -> BookEditorTestTag.INPUT_PUBLISHER
            InputType.YEAR -> BookEditorTestTag.INPUT_YEAR
            InputType.PAGES -> BookEditorTestTag.INPUT_PAGES
            InputType.ISBN -> BookEditorTestTag.INPUT_ISBN
            InputType.DESC -> BookEditorTestTag.INPUT_DESC
        }
        onNodeWithTag(tag).performClick()
        onNodeWithTag(tag).performTextInput(content)
    }

    fun AndroidComposeTestRule<*, *>.tapOnSave() {
        onNodeWithStringRes(R.string.btn_add).performClick()
    }
}

fun ComposeTestRule.onBookEditorScreen(scope: BookEditorRobot.() -> Unit) {
    verifyBookEditorScreenDisplayed()
    BookEditorRobot().apply(scope)
}

private fun ComposeTestRule.verifyBookEditorScreenDisplayed() {
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
