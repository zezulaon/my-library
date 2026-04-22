package dev.zezula.books.tests

import androidx.compose.ui.test.ExperimentalTestApi
import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.fake.FakeIsbnScannerController
import dev.zezula.books.domain.usecases.CreateShelfUseCase
import dev.zezula.books.tests.robot.AddBookOption
import dev.zezula.books.tests.robot.DrawerItemType
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onScannerScreen
import dev.zezula.books.tests.utils.bookHobit
import dev.zezula.books.tests.utils.bookNeverwhere
import dev.zezula.books.tests.utils.tapOnNavigateUp
import dev.zezula.books.tests.utils.testBooksData
import dev.zezula.books.tests.utils.testShelvesData
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class BookScannerInstrumentedTest : BaseInstrumentedTest() {

    private lateinit var fakeScanner: FakeIsbnScannerController

    private val createShelfUseCase: CreateShelfUseCase by inject()

    @Before
    fun setUp() {
        fakeScanner = getKoin().get()
        fakeScanner.reset()

        runBlocking {
            testShelvesData.forEach {
                createShelfUseCase(
                    shelfTitle = it.title,
                )
            }
        }
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

    @Test
    fun when_bulk_scanning_is_open_then_scanned_book_are_added_to_the_specific_shelf() {
        grantCameraPermission()

        val firstCustomShelfTitle = testShelvesData.first().title
        val bookHobit = testBooksData.bookHobit
        val bookNeverwhere = testBooksData.bookNeverwhere

        onApp(composeTestRule) {
            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.CustomShelf(firstCustomShelfTitle))
                tapOnAddBook(AddBookOption.BULK_SCAN)
            }

            onScannerScreen {
                fakeScanner.emitScan(bookHobit.isbn)
                assertBookAddedConfirmationDisplayed(bookHobit.title, firstCustomShelfTitle)

                tapOnScanAnother()

                fakeScanner.emitScan(bookNeverwhere.isbn)
                assertBookAddedConfirmationDisplayed(bookNeverwhere.title, firstCustomShelfTitle)

                tapOnCancelScanning()
            }

            onHomeScreen {
                assertBookTitleIsDisplayed(bookHobit.title)
                assertBookTitleIsDisplayed(bookNeverwhere.title)
            }
        }
    }
}
