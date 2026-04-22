package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zezula.books.R
import dev.zezula.books.testtag.ScanBarcodeTestTag

class ScannerRobot(private val rule: AndroidComposeTestRule<*, *>) {

    fun assertPermissionInfoDisplayed() {
        with(rule) {
            onNodeWithText(activity.getString(R.string.scanner_perm_required))
                .assertIsDisplayed()
        }
    }

    fun assertCameraComponentDisplayed() {
        with(rule) {
            onNodeWithTag(ScanBarcodeTestTag.CONTAINER_SCANNER)
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

    fun assertNoBookFoundDisplayed() {
        with(rule) {
            onNodeWithText(activity.getString(R.string.search_no_book_found_for_isbn))
                .assertIsDisplayed()
            onNodeWithText(activity.getString(R.string.search_btn_scan_again))
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }

    fun assertBookAddedConfirmationDisplayed(bookTitle: String, shelfTitle: String) {
        with(rule) {
            val label = buildString {
                append(bookTitle)
                append(" ")
                append(activity.getString(R.string.search_book_added_appendable_label))
                append(" ")
                append(shelfTitle)
            }
            onNodeWithText(label, substring = true, useUnmergedTree = true).assertIsDisplayed()
        }
    }

    fun tapOnScanAnother() {
        with(rule) {
            onNodeWithText(activity.getString(R.string.search_btn_scan_another)).performClick()
        }
    }

    fun tapOnCancelScanning() {
        with(rule) {
            onNodeWithText(activity.getString(R.string.search_btn_cancel_scan)).performClick()
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
