package dev.zezula.books.tests.robot

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
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.R
import dev.zezula.books.testtag.ManageShelvesTestTag

class ManageShelvesRobot : BackRobotFeature by BackRobotFeatureImpl() {

    fun AndroidComposeTestRule<*, *>.addNewShelf(name: String) {
        val label = activity.getString(R.string.shelves_btn_new_shelf)
        onNodeWithText(text = label, useUnmergedTree = true)
            .performClick()

        onNodeWithTag(ManageShelvesTestTag.INPUT_SHELF_NAME)
            .performTextInput(name)

        onNodeWithText(activity.getString(R.string.shelves_dialog_btn_save))
            .performClick()
    }

    fun AndroidComposeTestRule<*, *>.tapOnDeleteButton(shelfTitle: String) {
        // BTN_EXPAND_SHELF is inside ManageShelvesTestTag.CONTAINER_SHELF_ITEM and ManageShelvesTestTag.CONTAINER_SHELF_ITEM also has child with shelf name text
        val shelfItemMatcher =
            hasTestTag(ManageShelvesTestTag.CONTAINER_SHELF_ITEM) and
                hasAnyDescendant(hasText(shelfTitle))

        val isInShelfItem = hasAnyAncestor(shelfItemMatcher)

        onNode(hasTestTag(ManageShelvesTestTag.BTN_EXPAND_SHELF) and isInShelfItem)
            .performClick()

        onNode(hasText(activity.getString(R.string.shelves_btn_remove)) and isInShelfItem)
            .performClick()
    }

    fun ComposeTestRule.assertShelfIsDisplayed(name: String) {
        onNodeWithText(name).assertIsDisplayed()
    }

    fun ComposeTestRule.assertShelfDoesNotExist(name: String) {
        onNodeWithText(name).assertDoesNotExist()
    }
}

fun ComposeTestRule.onManageShelvesScreen(scope: ManageShelvesRobot.() -> Unit) {
    verifyManageShelvesScreenIsDisplayed()
    ManageShelvesRobot().apply(scope)
}

private fun ComposeTestRule.verifyManageShelvesScreenIsDisplayed() {
    onNodeWithTag(ManageShelvesTestTag.ROOT).assertIsDisplayed()
}
