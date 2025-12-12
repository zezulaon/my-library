package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.core.model.previewBooks
import dev.zezula.books.core.model.previewShelves
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.domain.usecases.CreateShelfUseCase
import dev.zezula.books.tests.robot.DrawerItemType
import dev.zezula.books.tests.robot.HomeCategory
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onManageShelvesScreen
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class ShelvesManagementInstrumentedTest : BaseInstrumentedTest() {

    private val addOrUpdateLibraryBookUseCase: AddOrUpdateLibraryBookUseCase by inject()
    private val createShelfUseCase: CreateShelfUseCase by inject()

    private val testPreviewBooks = previewBooks

    @Before
    fun setup() = runTest {
        testPreviewBooks.forEach {
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
        previewShelves.forEach {
            createShelfUseCase(
                shelfTitle = it.title,
            )
        }
    }

    @Test
    fun when_shelf_is_added_manually_then_it_appears_in_the_app() {
        val customShelfTitle = "My test shelf"

        composeTestRule.apply {
            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.ManageShelves)
            }

            onManageShelvesScreen {
                addNewShelf(customShelfTitle)
                assertShelfIsDisplayed(customShelfTitle)
                tapOnNavigateUp()
            }

            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.CustomShelf(customShelfTitle))
                assertCategoryDisplayed(HomeCategory.Custom(customShelfTitle))
            }
        }
    }

    @Test
    fun when_existing_shelf_is_deleted_then_it_no_longer_appears_in_the_app() {
        // FIXME: first add a book to shelf, then assert shelf has 1 book, then delete and then assert shelf is gone and book has no shelf
        val shelfToDeleteTitle = previewShelves
            .first()
            .title

        composeTestRule.apply {
            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.ManageShelves)
            }

            onManageShelvesScreen {
                tapOnDeleteButton(shelfToDeleteTitle)
                assertShelfDoesNotExist(shelfToDeleteTitle)
            }
        }
    }

    @Test
    fun when_existing_shelf_is_edited_then_new_title_appears_in_the_app() {
        val shelfToEditTitle = previewShelves.first().title
        val newShelfTitle = "Updated Shelf Title"

        composeTestRule.apply {
            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.ManageShelves)
            }

            onManageShelvesScreen {
                editShelf(shelfToEditTitle = shelfToEditTitle, newShelfTitle = newShelfTitle)
                assertShelfIsDisplayed(newShelfTitle)
                tapOnNavigateUp()
            }

            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.CustomShelf(newShelfTitle))
                assertCategoryDisplayed(HomeCategory.Custom(newShelfTitle))
            }
        }
    }
}
