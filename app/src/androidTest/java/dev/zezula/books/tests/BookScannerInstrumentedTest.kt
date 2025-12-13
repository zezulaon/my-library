package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.tests.robot.AddBookOption
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onScannerScreen
import org.junit.Test

class BookScannerInstrumentedTest : BaseInstrumentedTest() {

    @Test
    fun when_scanner_screen_is_opened_without_camera_permission_then_permission_info_is_displayed() {
        composeTestRule.apply {
            onHomeScreen {
                tapOnAddBook(AddBookOption.SCAN)
            }
            onScannerScreen {
                assertPermissionInfoDisplayed()
                assertAllowCameraButtonDisplayed()
            }
        }
    }
}
