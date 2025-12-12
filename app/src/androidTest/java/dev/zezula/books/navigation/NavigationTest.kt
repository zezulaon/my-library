package dev.zezula.books.navigation

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.R
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.core.model.previewBooks
import dev.zezula.books.core.model.previewShelves
import dev.zezula.books.core.rules.KoinTestRule
import dev.zezula.books.core.rules.PrintSemanticsOnFailureRule
import dev.zezula.books.core.rules.ScreenshotOnFailureRule
import dev.zezula.books.core.utils.test.homeBtnScanBarcode
import dev.zezula.books.di.appInstrumentedTestModule
import dev.zezula.books.di.appModule
import dev.zezula.books.di.flavoredAppModule
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.domain.usecases.CreateShelfUseCase
import dev.zezula.books.testtag.HomeTestTag
import dev.zezula.books.testtag.ManageShelvesTestTag
import dev.zezula.books.ui.MyLibraryMainActivity
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

class NavigationTest : KoinTest {

    private val addOrUpdateLibraryBookUseCase: AddOrUpdateLibraryBookUseCase by inject()
    private val createShelfUseCase: CreateShelfUseCase by inject()

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule(
        modules = listOf(appModule, flavoredAppModule, appInstrumentedTestModule),
    )

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MyLibraryMainActivity>()

    @get:Rule(order = 2)
    val printSemanticsOnFailureRule = PrintSemanticsOnFailureRule(composeTestRule)

    @get:Rule(order = 3)
    val screenshotOnFailureRule = ScreenshotOnFailureRule(composeTestRule)

    @Before
    fun setup() = runTest {
        // Add preview shelves to the database
        previewShelves.forEach {
            createShelfUseCase(
                shelfTitle = it.title,
            )
        }

        previewBooks.forEach {
            addOrUpdateLibraryBookUseCase(
                bookId = null,
                bookFormData = BookFormData(
                    title = it.title,
                    author = it.author,
                    description = it.description,
                    isbn = it.isbn,
                    publisher = it.publisher,
                    yearPublished = it.yearPublished,
                    pageCount = it.pageCount,
                    thumbnailLink = it.thumbnailLink,
                ),
            )
        }
    }

    @Test
    fun scannerScreen_firstLaunch_showsMissingPermissionInfo() {
        composeTestRule.apply {
            // GIVEN the user is on "Scan a book" screen
            onNodeWithTag(HomeTestTag.BTN_ADD_BOOK).performClick()
            onNodeWithTag(homeBtnScanBarcode).performClick()
            // THEN information about missing camera permission is displayed
            onNodeWithText(activity.getString(R.string.scanner_perm_required))
                .assertIsDisplayed()
            // AND button that allows permission is displayed
            onNodeWithText(activity.getString(R.string.scanner_btn_allow_camera))
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }
}
