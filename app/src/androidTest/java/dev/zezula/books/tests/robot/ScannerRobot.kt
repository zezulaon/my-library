package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.zezula.books.R
import dev.zezula.books.testtag.ScanBarcodeTestTag

class ScannerRobot {

    fun AndroidComposeTestRule<*, *>.assertPermissionInfoDisplayed() {
        onNodeWithText(activity.getString(R.string.scanner_perm_required))
            .assertIsDisplayed()
    }

    fun AndroidComposeTestRule<*, *>.assertAllowCameraButtonDisplayed() {
        onNodeWithText(activity.getString(R.string.scanner_btn_allow_camera))
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}

fun AndroidComposeTestRule<*, *>.onScannerScreen(scope: ScannerRobot.() -> Unit) {
    verifyScannerScreenIsDisplayed()
    ScannerRobot().apply(scope)
}

private fun AndroidComposeTestRule<*, *>.verifyScannerScreenIsDisplayed() {
    onNodeWithTag(ScanBarcodeTestTag.ROOT)
        .assertIsDisplayed()
}
