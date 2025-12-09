package dev.zezula.books.navigation

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSiblings
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
import dev.zezula.books.core.utils.test.createShelfInputTitle
import dev.zezula.books.core.utils.test.detailShelfCheckbox
import dev.zezula.books.core.utils.test.homeBtnScanBarcode
import dev.zezula.books.core.utils.test.homeNavDrawer
import dev.zezula.books.core.utils.test.homeNavDrawerShelfItem
import dev.zezula.books.core.utils.test.manageShelvesBtnExpand
import dev.zezula.books.core.utils.test.manageShelvesShelfItem
import dev.zezula.books.di.appInstrumentedTestModule
import dev.zezula.books.di.appModule
import dev.zezula.books.di.flavoredAppModule
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.domain.usecases.CreateShelfUseCase
import dev.zezula.books.testtag.HomeTestTag
import dev.zezula.books.ui.MyLibraryMainActivity
import dev.zezula.books.waitUntilExists
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

    @Test
    fun createBookScreen_saveEmptyForm_showsError() {
        composeTestRule.apply {
            // GIVEN the user is on "Create" screen
            onNodeWithTag(HomeTestTag.BTN_ADD_BOOK).performClick()
            onNodeWithTag(HomeTestTag.BTN_ADD_BOOK_MANUALLY).performClick()
            // WHEN user tries to save the empty form
            onNodeWithText(activity.getString(R.string.btn_add))
                .assertIsDisplayed()
                .performClick()
            // THEN snackbar with error message is displayed
            onNodeWithText(activity.getString(R.string.invalid_input_form)).assertIsDisplayed()
        }
    }

    @Test
    fun bookDetailScreen_clickOnShelfCheckbox_bookIsAddedToShelf() {
        val bookToCheck = previewBooks.first()
        val shelfToCheck = previewShelves.first()

        composeTestRule.apply {
            // Wait till list items are fetched and visible
            waitUntilExists(hasText(previewBooks.first().title!!))

            // GIVEN the user is on "Detail" screen
            onNodeWithText(bookToCheck.title!!).performClick()
            // AND the "shelves" tab is selected
            onNodeWithText(activity.getString(R.string.screen_detail_tab_shelves)).performClick()
            // AND the "shelf to check" is displayed
            onNodeWithText(shelfToCheck.title).assertIsDisplayed()
                // AND the "shelf to check" is unchecked
                .onSiblings().onFirst().assertIsOff()
                // WHEN checkbox is turned on
                .performClick()
            // THEN the checkbox is turned on
            onAllNodesWithTag(detailShelfCheckbox).filter(isOn()).assertCountEquals(1)
            // AND book list for this shelf contains the correct book
            onNodeWithContentDescription(activity.getString(R.string.content_desc_navigate_back)).performClick()
            onNodeWithTag(homeNavDrawer).performClick()
            onNodeWithTag("$homeNavDrawerShelfItem${shelfToCheck.title}").performClick()
            onNodeWithText(bookToCheck.title!!).assertIsDisplayed()
        }
    }

    @Test
    fun manageShelvesScreen_newShelf_isCreated() {
        val newShelfTitle = "New Test Shelf 123"

        composeTestRule.apply {
            // GIVEN the user is on "Manage Shelves" screen
            onNodeWithTag(homeNavDrawer).performClick()
            onNodeWithText(activity.getString(R.string.drawer_item_manage_shelves)).performClick()
            // WHEN user clicks on New Shelf button
            onNodeWithText(
                text = activity.getString(R.string.shelves_btn_new_shelf),
                useUnmergedTree = true,
            ).performClick()
            // AND fills the name of the new shelf
            onNodeWithTag(createShelfInputTitle).performTextInput(newShelfTitle)
            // AND user saves the shelf
            onNodeWithText(activity.getString(R.string.shelves_dialog_btn_save)).performClick()
            // THEN new shelf was added to the list of shelves and is displayed
            onNodeWithText(newShelfTitle).assertIsDisplayed()
            // AND new shelf is also displayed in navigation bar
            onNodeWithContentDescription(activity.getString(R.string.content_desc_navigate_back)).performClick()
            onNodeWithText(newShelfTitle).assertExists()
        }
    }

    @Test
    fun manageShelvesScreen_clickOnDelete_shelfIsDeleted() {
        composeTestRule.apply {
            // GIVEN the user is on "Manage Shelves" screen
            onNodeWithTag(homeNavDrawer).performClick()
            onNodeWithText(activity.getString(R.string.drawer_item_manage_shelves)).performClick()
            // WHEN user clicks on expand button
            onAllNodesWithTag(manageShelvesBtnExpand).onFirst().performClick()
            // AND user clicks on delete button
            onNodeWithText(activity.getString(R.string.shelves_btn_remove)).performClick()
            // AND user goes back to home screen and back to "Manage Shelves" screen
            onNodeWithContentDescription(activity.getString(R.string.content_desc_navigate_back)).performClick()
            onNodeWithTag(homeNavDrawer).performClick()
            onNodeWithText(activity.getString(R.string.drawer_item_manage_shelves)).performClick()
            // THEN the shelves list is updated (shelf is deleted)
            onAllNodesWithTag(manageShelvesShelfItem).assertCountEquals(previewShelves.size - 1)
        }
    }

    @Test
    fun manageShelvesScreen_clickOnEdit_updateNewTitle_newTitleIsSaved() {
        val updatedShelfTitle = "updated shelf title"

        composeTestRule.apply {
            // GIVEN the user is on "Manage Shelves" screen
            onNodeWithTag(homeNavDrawer).performClick()
            onNodeWithText(activity.getString(R.string.drawer_item_manage_shelves)).performClick()
            // WHEN user clicks on expand button
            onAllNodesWithTag(manageShelvesBtnExpand).onFirst().performClick()
            // AND user clicks on edit button
            onNodeWithText(activity.getString(R.string.shelves_btn_edit)).performClick()
            // AND user updates shelf title
            onNodeWithTag(createShelfInputTitle).apply { performTextClearance() }.performTextInput(updatedShelfTitle)
            // AND user saves the shelf
            onNodeWithText(activity.getString(R.string.shelves_dialog_btn_update)).performClick()
            // THEN new shelf was updated in the list of shelves
            onNodeWithText(updatedShelfTitle).assertIsDisplayed()
        }
    }
}
