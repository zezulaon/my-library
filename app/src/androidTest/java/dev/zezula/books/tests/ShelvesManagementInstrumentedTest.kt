package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.domain.usecases.CreateShelfUseCase
import dev.zezula.books.tests.robot.BookDetailTab
import dev.zezula.books.tests.robot.DrawerItemType
import dev.zezula.books.tests.robot.HomeCategory
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onManageShelvesScreen
import dev.zezula.books.tests.utils.testBooksData
import dev.zezula.books.tests.utils.testShelvesData
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class ShelvesManagementInstrumentedTest : BaseInstrumentedTest() {

    private val addOrUpdateLibraryBookUseCase: AddOrUpdateLibraryBookUseCase by inject()
    private val createShelfUseCase: CreateShelfUseCase by inject()

    @Before
    fun setup() = runTest {
        testBooksData.forEach {
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
        testShelvesData.forEach {
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
        val shelfToDeleteTitle = testShelvesData
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
        val shelfToEditTitle = testShelvesData.first().title
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

    @Test
    fun when_book_is_added_to_shelf_and_then_removed_from_shelf_then_it_is_no_longer_in_the_shelf() {
        val book = testBooksData.first()
        val shelfTitle = testShelvesData
            .first()
            .title

        composeTestRule.apply {
            onHomeScreen {
                tapOnBookTitle(book.title!!)
            }

            onBookDetailScreen {
                tapOnTab(BookDetailTab.SHELVES)
                toggleShelfSelection(shelfTitle)
                tapOnNavigateUp()
            }

            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.CustomShelf(shelfTitle))
                assertCategoryDisplayed(HomeCategory.Custom(shelfTitle))
                assertBookTitleIsDisplayed(book.title!!)

                tapOnBookTitle(book.title!!)
            }

            onBookDetailScreen {
                tapOnTab(BookDetailTab.SHELVES)
                toggleShelfSelection(shelfTitle)
                tapOnNavigateUp()
            }

            onHomeScreen {
                assertCategoryDisplayed(HomeCategory.Custom(shelfTitle))
                assertBookTitleDoesNotExist(book.title!!)
            }
        }
    }

    @Test
    fun when_book_is_added_to_shelf_and_shelf_is_deleted_then_book_is_no_longer_in_shelf() {
        val book = testBooksData.first()
        val firstShelfTitle = testShelvesData[0].title
        val secondShelfTitle = testShelvesData[1].title

        composeTestRule.apply {
            onHomeScreen {
                tapOnBookTitle(book.title!!)
            }

            onBookDetailScreen {
                tapOnTab(BookDetailTab.SHELVES)
                toggleShelfSelection(firstShelfTitle)
                toggleShelfSelection(secondShelfTitle)
                tapOnNavigateUp()
            }

            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.CustomShelf(firstShelfTitle))
                assertBookTitleIsDisplayed(book.title!!)
                tapOnBookTitle(book.title!!)
            }

            onBookDetailScreen {
                tapOnTab(BookDetailTab.SHELVES)
                tapOnManageShelvesButton()
            }

            onManageShelvesScreen {
                assertShelfWithBookCountDisplayed(shelfTitle = firstShelfTitle, count = 1)
                tapOnDeleteButton(firstShelfTitle)
                tapOnNavigateUp()
            }

            onBookDetailScreen {
                assertShelfDoesNotExist(firstShelfTitle)
                tapOnNavigateUp()
            }

            onHomeScreen {
                openNavigationDrawer()
                assertNavigationDrawerItemDoesNotExist(DrawerItemType.CustomShelf(firstShelfTitle))
            }
        }
    }
}
