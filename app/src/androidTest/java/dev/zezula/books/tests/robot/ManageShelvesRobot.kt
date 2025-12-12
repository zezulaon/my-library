package dev.zezula.books.tests.robot

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.R
import dev.zezula.books.tests.utils.onNodeWithTextStringRes
import dev.zezula.books.testtag.ManageShelvesTestTag

class ManageShelvesRobot : BackRobotFeature by BackRobotFeatureImpl() {

    fun AndroidComposeTestRule<*, *>.addNewShelf(shelfTitle: String) {
        val label = activity.getString(R.string.shelves_btn_new_shelf)
        onNodeWithText(text = label, useUnmergedTree = true)
            .performClick()

        onNodeWithTag(ManageShelvesTestTag.INPUT_SHELF_NAME)
            .performTextInput(shelfTitle)

        onNodeWithTextStringRes(R.string.shelves_dialog_btn_save)
            .performClick()
    }

    fun AndroidComposeTestRule<*, *>.editShelf(shelfToEditTitle: String, newShelfTitle: String) {
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

    fun AndroidComposeTestRule<*, *>.tapOnDeleteButton(shelfToDeleteTitle: String) {
        expandShelfCard(shelfToDeleteTitle)

        onNode(hasText(activity.getString(R.string.shelves_btn_remove)) and isInShelfItemContainer(shelfToDeleteTitle))
            .performClick()
    }

    private fun ComposeTestRule.expandShelfCard(shelfTitle: String) {
        onNode(hasTestTag(ManageShelvesTestTag.BTN_EXPAND_SHELF) and isInShelfItemContainer(shelfTitle))
            .performClick()
    }

    fun ComposeTestRule.assertShelfIsDisplayed(name: String) {
        onNodeWithText(name).assertIsDisplayed()
    }

    fun ComposeTestRule.assertShelfDoesNotExist(name: String) {
        onNodeWithText(name).assertDoesNotExist()
    }

    fun AndroidComposeTestRule<*, *>.assertShelfWithBookCountDisplayed(shelfTitle: String, count: Int) {
        val countString = activity
            .resources.getQuantityString(R.plurals.shelves_label_books_count, count, count)
        onNode(hasText(countString) and isInShelfItemContainer(shelfTitle)).assertIsDisplayed()
    }

    private fun isInShelfItemContainer(shelfTitle: String): SemanticsMatcher {
        val shelfItemMatcher =
            hasTestTag(ManageShelvesTestTag.CONTAINER_SHELF_ITEM) and
                hasAnyDescendant(hasText(shelfTitle))

        return hasAnyAncestor(shelfItemMatcher)
    }
}

fun ComposeTestRule.onManageShelvesScreen(scope: ManageShelvesRobot.() -> Unit) {
    verifyManageShelvesScreenIsDisplayed()
    ManageShelvesRobot().apply(scope)
}

private fun ComposeTestRule.verifyManageShelvesScreenIsDisplayed() {
    onNodeWithTag(ManageShelvesTestTag.ROOT).assertIsDisplayed()
}
