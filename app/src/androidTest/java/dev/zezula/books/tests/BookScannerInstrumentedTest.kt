package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.model.test.bookHobit
import dev.zezula.books.core.model.test.testBooksData
import dev.zezula.books.tests.robot.AddBookOption
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onScannerScreen
import org.junit.Test

class BookScannerInstrumentedTest : BaseInstrumentedTest() {

    @Test
    fun when_scanner_screen_is_opened_without_camera_permission_then_permission_info_is_displayed() {
        onApp(composeTestRule) {
            onHomeScreen {
                tapOnAddBook(AddBookOption.SCAN)
            }
            onScannerScreen {
                assertPermissionInfoDisplayed()
                assertAllowCameraButtonDisplayed()
            }
        }
    }

    @Test
    fun when_scanner_screen_is_opened_with_camera_permission_then_camera_is_active() {
        grantCameraPermission()

        onApp(composeTestRule) {
            onHomeScreen {
                tapOnAddBook(AddBookOption.SCAN)
            }
            onScannerScreen {
                assertCameraComponentDisplayed()
            }
        }
    }

    @Test
    fun when_isbn_is_scanned_then_book_is_added_to_library() {
        grantCameraPermission()

        onApp(composeTestRule) {
            onHomeScreen {
                tapOnAddBook(AddBookOption.SCAN)
            }

            onScannerScreen {
                simulateScan()
            }

            onBookDetailScreen {
                assertBookDisplayed(testBooksData.bookHobit)
            }
        }
    }
}
