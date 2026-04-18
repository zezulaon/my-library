package dev.zezula.books.tests

import androidx.compose.ui.test.ExperimentalTestApi
import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.fake.FakeIsbnScannerController
import dev.zezula.books.tests.robot.AddBookOption
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onScannerScreen
import dev.zezula.books.tests.utils.bookHobit
import dev.zezula.books.tests.utils.tapOnNavigateUp
import dev.zezula.books.tests.utils.testBooksData
import org.junit.Before
import org.junit.Test

class BookScannerInstrumentedTest : BaseInstrumentedTest() {

    private lateinit var fakeScanner: FakeIsbnScannerController

    @Before
    fun setUp() {
        fakeScanner = getKoin().get()
        fakeScanner.reset()
    }

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
    fun when_scanner_screen_is_opened_with_camera_permission_then_camera_component_is_launched() {
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
                fakeScanner.emitScan(testBooksData.bookHobit.isbn)
            }

            onBookDetailScreen {
                assertBookDisplayed(testBooksData.bookHobit)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun when_the_scanned_book_is_deleted_then_it_can_be_rescanned_again() {
        grantCameraPermission()

        onApp(composeTestRule) {
            onHomeScreen {
                tapOnAddBook(AddBookOption.SCAN)
            }

            val bookHobit = testBooksData.bookHobit
            onScannerScreen {
                fakeScanner.emitScan(bookHobit.isbn)
            }

            onBookDetailScreen {
                assertBookDisplayed(bookHobit)
                tapOnDeleteButton()
                confirmDeletion()
            }

            onHomeScreen {
                tapOnAddBook(AddBookOption.SCAN)
            }

            onScannerScreen {
                fakeScanner.emitScan(bookHobit.isbn)
            }

            onBookDetailScreen {
                assertBookDisplayed(bookHobit)
                tapOnNavigateUp()
            }

            onHomeScreen {
                assertBookTitleIsDisplayed(bookHobit.title)
            }
        }
    }

    @Test
    fun when_isbn_is_scanned_and_no_book_found_then_info_is_displayed() {
        grantCameraPermission()

        onApp(composeTestRule) {
            onHomeScreen {
                tapOnAddBook(AddBookOption.SCAN)
            }

            onScannerScreen {
                fakeScanner.emitScan("000000000")
                assertNoBookFoundDisplayed()
            }
        }
    }
}
