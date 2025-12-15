package dev.zezula.books.tests.robot

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zezula.books.R
import dev.zezula.books.testtag.HomeTestTag

class HomeRobot(private val rule: AndroidComposeTestRule<*, *>) {

    fun openNavigationDrawer() {
        rule.onNodeWithTag(HomeTestTag.BTN_OPEN_NAV_DRAWER).performClick()
    }

    fun tapOnNavigationDrawerItem(item: DrawerItemType) {
        getNavigationDrawerNode(item)
            .assertIsDisplayed()
            .performClick()
    }

    fun assertNavigationDrawerItemDoesNotExist(item: DrawerItemType) {
        getNavigationDrawerNode(item)
            .assertDoesNotExist()
    }

    private fun getNavigationDrawerNode(item: DrawerItemType): SemanticsNodeInteraction {
        with(rule) {
            val isInNavDrawer = hasAnyAncestor(hasTestTag(HomeTestTag.CONTAINER_NAV_DRAWER))
            return when (item) {
                is DrawerItemType.ManageShelves -> {
                    val label = activity.getString(R.string.drawer_item_manage_shelves)
                    onNode(hasText(label) and isInNavDrawer)
                }

                is DrawerItemType.CustomShelf -> {
                    onNode(hasText(item.name) and isInNavDrawer)
                }
            }
        }
    }

    fun tapOnAddBook(option: AddBookOption) {
        with(rule) {
            onNodeWithTag(HomeTestTag.BTN_ADD_BOOK).performClick()
            when (option) {
                AddBookOption.MANUALLY -> {
                    onNodeWithTag(HomeTestTag.BTN_ADD_BOOK_MANUALLY).performClick()
                }

                AddBookOption.SCAN -> {
                    onNodeWithTag(HomeTestTag.BTN_SCAN_BARCODE).performClick()
                }
            }
        }
    }

    fun assertCategoryDisplayed(category: HomeCategory) {
        with(rule) {
            val categoryTitle: String = when (category) {
                is HomeCategory.AllBooks -> activity.getString(R.string.home_shelf_title_all_books)
                is HomeCategory.Custom -> category.name
            }
            val hasToolbarAncestor = hasAnyAncestor(hasTestTag(HomeTestTag.CONTAINER_TOOLBAR))
            onNode(hasText(categoryTitle) and hasToolbarAncestor)
                .assertIsDisplayed()
        }
    }

    fun assertToolbarBookSize(size: Int) {
        with(rule) {
            val hasToolbarAncestor = hasAnyAncestor(hasTestTag(HomeTestTag.CONTAINER_TOOLBAR))
            val numberBooksFormatted = activity.resources.getQuantityString(
                R.plurals.home_number_of_books_subtitle,
                size,
                size,
            )
            onNode(hasText(numberBooksFormatted) and hasToolbarAncestor)
                .assertIsDisplayed()
        }
    }

    fun tapOnBookTitle(bookTitle: String) {
        rule.onNodeWithText(bookTitle).performClick()
    }

    fun assertBookTitleDoesNotExist(bookTitle: String) {
        rule.onNodeWithText(bookTitle).assertDoesNotExist()
    }

    fun assertBookTitleIsDisplayed(bookTitle: String) {
        rule.onNodeWithText(bookTitle).assertIsDisplayed()
    }
}

fun AppRobot.onHomeScreen(block: HomeRobot.() -> Unit) {
    rule.verifyHomeScreenDisplayed()
    HomeRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifyHomeScreenDisplayed() {
    onNodeWithTag(HomeTestTag.ROOT).assertIsDisplayed()
}

enum class AddBookOption {
    MANUALLY,
    SCAN,
}

sealed interface HomeCategory {
    object AllBooks : HomeCategory
    data class Custom(val name: String) : HomeCategory
}

sealed interface DrawerItemType {
    object ManageShelves : DrawerItemType
    data class CustomShelf(val name: String) : DrawerItemType
}
