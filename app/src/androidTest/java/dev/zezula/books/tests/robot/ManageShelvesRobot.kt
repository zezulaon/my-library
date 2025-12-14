package dev.zezula.books.tests.robot

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
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
import dev.zezula.books.testtag.ManageShelvesTestTag

class ManageShelvesRobot(private val rule: AndroidComposeTestRule<*, *>) {

    fun addNewShelf(shelfTitle: String) {
        with(rule) {
            val label = activity.getString(R.string.shelves_btn_new_shelf)
            onNodeWithText(text = label, useUnmergedTree = true)
                .performClick()

            onNodeWithTag(ManageShelvesTestTag.INPUT_SHELF_NAME)
                .performTextInput(shelfTitle)

            onNodeWithTextStringRes(R.string.shelves_dialog_btn_save)
                .performClick()
        }
    }

    fun editShelf(shelfToEditTitle: String, newShelfTitle: String) {
        with(rule) {
            expandShelfCard(shelfToEditTitle)

            val editLabel = activity.getString(R.string.shelves_btn_edit)
            onNode(hasText(editLabel) and isInShelfItemContainer(shelfToEditTitle))
                .performClick()

            onNodeWithTag(ManageShelvesTestTag.INPUT_SHELF_NAME)
                .apply {
                    performTextClearance()
                    performTextInput(newShelfTitle)
                }

            onNodeWithTextStringRes(R.string.shelves_dialog_btn_update)
                .performClick()
        }
    }

    fun tapOnDeleteButton(shelfToDeleteTitle: String) {
        with(rule) {
            expandShelfCard(shelfToDeleteTitle)

            onNode(hasText(activity.getString(R.string.shelves_btn_remove)) and isInShelfItemContainer(shelfToDeleteTitle))
                .performClick()
        }
    }

    private fun expandShelfCard(shelfTitle: String) {
        rule.onNode(hasTestTag(ManageShelvesTestTag.BTN_EXPAND_SHELF) and isInShelfItemContainer(shelfTitle))
            .performClick()
    }

    fun assertShelfIsDisplayed(name: String) {
        rule.onNodeWithText(name).assertIsDisplayed()
    }

    fun assertShelfDoesNotExist(name: String) {
        rule.onNodeWithText(name).assertDoesNotExist()
    }

    fun assertShelfWithBookCountDisplayed(shelfTitle: String, count: Int) {
        with(rule) {
            val countString = activity
                .resources.getQuantityString(R.plurals.shelves_label_books_count, count, count)
            onNode(hasText(countString) and isInShelfItemContainer(shelfTitle)).assertIsDisplayed()
        }
    }

    private fun isInShelfItemContainer(shelfTitle: String): SemanticsMatcher {
        val shelfItemMatcher =
            hasTestTag(ManageShelvesTestTag.CONTAINER_SHELF_ITEM) and
                hasAnyDescendant(hasText(shelfTitle))

        return hasAnyAncestor(shelfItemMatcher)
    }
}

fun AppRobot.onManageShelvesScreen(block: ManageShelvesRobot.() -> Unit) {
    rule.verifyManageShelvesScreenIsDisplayed()
    ManageShelvesRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyManageShelvesScreenIsDisplayed() {
    onNodeWithTag(ManageShelvesTestTag.ROOT).assertIsDisplayed()
}
