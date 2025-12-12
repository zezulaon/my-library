package dev.zezula.books.tests

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.zezula.books.R
import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.utils.test.homeBtnScanBarcode
import dev.zezula.books.tests.utils.onNodeWithTextStringRes
import dev.zezula.books.testtag.HomeTestTag
import org.junit.Test

class BookScannerInstrumentedTest : BaseInstrumentedTest() {

    @Test
    fun scannerScreen_firstLaunch_showsMissingPermissionInfo() {
        composeTestRule.apply {
            // GIVEN the user is on "Scan a book" screen
            onNodeWithTag(HomeTestTag.BTN_ADD_BOOK).performClick()
            onNodeWithTag(homeBtnScanBarcode).performClick()
            // THEN information about missing camera permission is displayed
            onNodeWithTextStringRes(R.string.scanner_perm_required)
                .assertIsDisplayed()
            // AND button that allows permission is displayed
            onNodeWithTextStringRes(R.string.scanner_btn_allow_camera)
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }
}
