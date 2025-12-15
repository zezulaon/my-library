package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.zezula.books.R
import dev.zezula.books.testtag.ScanBarcodeTestTag

class ScannerRobot(private val rule: AndroidComposeTestRule<*, *>) {

    fun assertPermissionInfoDisplayed() {
        with(rule) {
            onNodeWithText(activity.getString(R.string.scanner_perm_required))
                .assertIsDisplayed()
        }
    }

    fun assertAllowCameraButtonDisplayed() {
        with(rule) {
            onNodeWithText(activity.getString(R.string.scanner_btn_allow_camera))
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }
}

fun AppRobot.onScannerScreen(block: ScannerRobot.() -> Unit) {
    rule.verifyScannerScreenIsDisplayed()
    ScannerRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyScannerScreenIsDisplayed() {
    onNodeWithTag(ScanBarcodeTestTag.ROOT)
        .assertIsDisplayed()
}
