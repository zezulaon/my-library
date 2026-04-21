package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zezula.books.testtag.AppInfoTestTag

class AppInfoRobot(val rule: AndroidComposeTestRule<*, *>) {

    fun tapOnExport() {
        rule.onNodeWithTag(AppInfoTestTag.BTN_EXPORT).performClick()
    }

    fun assertExportedFileIsDisplayed(fileNamePrefix: String) {
        rule.onNodeWithText(fileNamePrefix, substring = true, useUnmergedTree = true).assertIsDisplayed()
    }
}

fun AppRobot.onAppInfoScreen(block: AppInfoRobot.() -> Unit) {
    rule.verifyAppInfoScreenDisplayed()
    AppInfoRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyAppInfoScreenDisplayed() {
    onNodeWithTag(AppInfoTestTag.ROOT).assertIsDisplayed()
}
