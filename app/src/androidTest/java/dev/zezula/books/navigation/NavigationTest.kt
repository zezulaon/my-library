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
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSiblings
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import dev.zezula.books.KoinTestRule
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.model.shelf.previewShelves
import dev.zezula.books.data.source.db.AppDatabase
import dev.zezula.books.di.appInstrumentedTestModule
import dev.zezula.books.di.appModule
import dev.zezula.books.ui.MyLibraryMainActivity
import dev.zezula.books.util.createBookInputAuthor
import dev.zezula.books.util.createBookInputDesc
import dev.zezula.books.util.createBookInputIsbn
import dev.zezula.books.util.createBookInputPages
import dev.zezula.books.util.createBookInputPublisher
import dev.zezula.books.util.createBookInputTitle
import dev.zezula.books.util.createBookInputYear
import dev.zezula.books.util.createShelfInputTitle
import dev.zezula.books.util.detailShelfCheckbox
import dev.zezula.books.util.homeBtnAddBook
import dev.zezula.books.util.homeBtnAddBookManually
import dev.zezula.books.util.homeBtnScanBarcode
import dev.zezula.books.util.homeNavDrawer
import dev.zezula.books.util.manageShelvesBtnExpand
import dev.zezula.books.util.manageShelvesShelfItem
import dev.zezula.books.waitUntilExists
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

class NavigationTest : KoinTest {

    private val db: AppDatabase by inject()

    private val bookToCreate = Book(
        id = "123",
        title = "New title",
        author = "New author",
        description = "New desc",
        isbn = "987456",
        publisher = "newPublisher",
        yearPublished = 2000,
        pageCount = 10,
        thumbnailLink = null,
        dateAdded = "2023-01-05T17:43:25.629",
    )

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule(
        // Override some production components with instrumented module
        modules = listOf(appModule, appInstrumentedTestModule),
    )

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MyLibraryMainActivity>()

    @After
    fun closeDb() {
        db.close()
    }

    @Test(expected = NoActivityResumedException::class)
    fun home_back_quitsApp() {
        composeTestRule.apply {
            // GIVEN the user is on detail screen
            // WHEN the user uses the system button/gesture to go back
            Espresso.pressBack()
            // THEN the app quits
        }
    }

    @Test
    fun scannerScreen_firstLaunch_showsMissingPermissionInfo() {
        composeTestRule.apply {
            // GIVEN the user is on "Scan a book" screen
            onNodeWithTag(homeBtnAddBook).performClick()
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
            onNodeWithTag(homeBtnAddBook).performClick()
            onNodeWithTag(homeBtnAddBookManually).performClick()
            // WHEN user tries to save the empty form
            onNodeWithText(activity.getString(R.string.btn_add))
                .assertIsDisplayed()
                .performClick()
            // THEN snackbar with error message is displayed
            onNodeWithText(activity.getString(R.string.invalid_input_form)).assertIsDisplayed()
        }
    }

    @Test
    fun createBookScreen_fillAndSaveForm_addsABook() {
        composeTestRule.apply {
            // GIVEN the user is on "Create" screen
            onNodeWithTag(homeBtnAddBook).performClick()
            onNodeWithTag(homeBtnAddBookManually).performClick()
            // WHEN user fills in all required data
            onNodeWithTag(createBookInputTitle).performTextInput(bookToCreate.title!!)
            onNodeWithTag(createBookInputAuthor).performTextInput(bookToCreate.author!!)
            onNodeWithTag(createBookInputPublisher).performTextInput(bookToCreate.publisher!!)
            onNodeWithTag(createBookInputYear).performTextInput(bookToCreate.yearPublished!!.toString())
            onNodeWithTag(createBookInputPages).performTextInput(bookToCreate.pageCount!!.toString())
            onNodeWithTag(createBookInputIsbn).performTextInput(bookToCreate.isbn!!)
            onNodeWithTag(createBookInputDesc).performTextInput(bookToCreate.description!!)
            // AND user saves the book
            onNodeWithText(activity.getString(R.string.btn_add)).performClick()
            // THEN "All books" screen is visible
            onAllNodesWithText(activity.getString(R.string.home_shelf_title_all_books)).onFirst().assertExists()
            // AND the given book is visible (was added)
            onNodeWithText(bookToCreate.title!!).assertIsDisplayed()
            // AND the book detail can be displayed with all the inserted data
            onNodeWithText(bookToCreate.title!!).performClick()
            onNodeWithText(bookToCreate.author!!).assertIsDisplayed()
            onNodeWithText(bookToCreate.publisher!!).assertIsDisplayed()
            onNodeWithText(bookToCreate.yearPublished!!.toString()).assertIsDisplayed()
            onNodeWithText(bookToCreate.pageCount!!.toString()).assertIsDisplayed()
            onNodeWithText(bookToCreate.isbn!!).assertIsDisplayed()
            onNodeWithText(bookToCreate.description!!).assertIsDisplayed()
        }
    }

    @Test
    fun updateBookScreen_fillBookData_updatesBook() {
        composeTestRule.apply {
            // Wait till list items are fetched and visible
            waitUntilExists(hasText(previewBooks.first().title!!))

            // GIVEN the user is on "Update" screen
            onNodeWithText(previewBooks.first().title!!).performClick()
            onNodeWithContentDescription(
                label = activity.getString(R.string.content_desc_edit),
                useUnmergedTree = true,
            ).performClick()
            // WHEN user fills in all required data
            onNodeWithTag(createBookInputTitle).apply { performTextClearance() }.performTextInput(bookToCreate.title!!)
            onNodeWithTag(createBookInputAuthor).apply { performTextClearance() }
                .performTextInput(bookToCreate.author!!)
            onNodeWithTag(createBookInputPublisher).apply { performTextClearance() }
                .performTextInput(bookToCreate.publisher!!)
            onNodeWithTag(createBookInputYear).apply { performTextClearance() }
                .performTextInput(bookToCreate.yearPublished!!.toString())
            onNodeWithTag(createBookInputPages).apply { performTextClearance() }
                .performTextInput(bookToCreate.pageCount!!.toString())
            onNodeWithTag(createBookInputIsbn).apply { performTextClearance() }.performTextInput(bookToCreate.isbn!!)
            onNodeWithTag(createBookInputDesc).apply { performTextClearance() }
                .performTextInput(bookToCreate.description!!)
            // AND user updates the book
            onNodeWithText(activity.getString(R.string.btn_update)).performClick()
            // THEN book detail screen and tab is visible and contains updated data
            onNodeWithText(activity.getString(R.string.screen_detail_tab_book))
                .assertExists()
            onNodeWithText(bookToCreate.title!!).assertIsDisplayed()
            onNodeWithText(bookToCreate.author!!).assertIsDisplayed()
            onNodeWithText(bookToCreate.publisher!!).assertIsDisplayed()
            onNodeWithText(bookToCreate.yearPublished!!.toString()).assertIsDisplayed()
            onNodeWithText(bookToCreate.pageCount!!.toString()).assertIsDisplayed()
            onNodeWithText(bookToCreate.isbn!!).assertIsDisplayed()
            onNodeWithText(bookToCreate.description!!).assertIsDisplayed()
        }
    }

    @Test
    fun bookDetailScreen_clickOnDelete_deletesBook() {
        val bookToDelete = previewBooks.first()

        composeTestRule.apply {
            // GIVEN the user is on "Detail" screen
            onNodeWithText(bookToDelete.title!!).performClick()
            // AND the "book to delete" detail is displayed
            onNodeWithText(bookToDelete.title!!).assertIsDisplayed()
            // WHEN user clicks on delete button
            onNodeWithContentDescription(
                label = activity.getString(R.string.content_desc_delete),
                useUnmergedTree = true,
            ).performClick()
            // AND user confirms the deletion
            onNodeWithText(activity.getString(R.string.detail_btn_confirm_delete)).performClick()
            // THEN "all books" home screen is displayed
            onAllNodesWithText(activity.getString(R.string.home_shelf_title_all_books)).onFirst().assertExists()
            // AND the book is not in the list (was deleted)
            onNodeWithText(bookToDelete.title!!).assertDoesNotExist()
            // AND the subtitle has correct number of books string
            onNodeWithText(
                activity.resources.getQuantityString(
                    R.plurals.home_number_of_books_subtitle,
                    previewBooks.size - 1,
                ),
            ).assertIsDisplayed()
        }
    }

    @Test
    fun bookDetailScreen_clickOnShelfCheckbox_bookIsAddedToShelf() {
        val bookToCheck = previewBooks.first()
        val shelfToCheck = previewShelves.first()

        composeTestRule.apply {
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
            onNodeWithText(shelfToCheck.title).performClick()
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
