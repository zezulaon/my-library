package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zezula.books.R
import dev.zezula.books.testtag.HomeTestTag

class HomeRobot {

    fun ComposeTestRule.tapOnAddBook(option: AddBookOption) {
        onNodeWithTag(HomeTestTag.BTN_ADD_BOOK).performClick()
        when (option) {
            AddBookOption.MANUALLY -> {
                onNodeWithTag(HomeTestTag.BTN_ADD_BOOK_MANUALLY).performClick()
            }
            AddBookOption.SCAN -> {
                TODO("Scan not implemented yet")
            }
        }
    }

    fun AndroidComposeTestRule<*, *>.assertCategoryDisplayed(category: HomeCategory) {
        val categoryTitle: String = when (category) {
            is HomeCategory.AllBooks -> activity.getString(R.string.home_shelf_title_all_books)
            is HomeCategory.Custom -> category.name
        }
        val hasToolbarAncestor = hasAnyAncestor(hasTestTag(HomeTestTag.CONTAINER_TOOLBAR))
        onNode(hasText(categoryTitle) and hasToolbarAncestor)
            .assertIsDisplayed()
    }

    fun AndroidComposeTestRule<*, *>.assertToolbarBookSize(size: Int) {
        val hasToolbarAncestor = hasAnyAncestor(hasTestTag(HomeTestTag.CONTAINER_TOOLBAR))

        val numberBooksFormatted = activity.resources.getQuantityString(
            R.plurals.home_number_of_books_subtitle,
            size,
            size,
        )

        onNode(hasText(numberBooksFormatted) and hasToolbarAncestor)
            .assertIsDisplayed()
    }

    fun ComposeTestRule.tapOnBookTitle(bookTitle: String) {
        onNodeWithText(bookTitle).performClick()
    }

    fun AndroidComposeTestRule<*, *>.assertBookTitleDoesNotExist(bookTitle: String) {
        onNodeWithText(bookTitle).assertDoesNotExist()
    }
}

fun ComposeTestRule.onHomeScreen(scope: HomeRobot.() -> Unit) {
    verifyHomeScreenDisplayed()
    HomeRobot().apply(scope)
}

private fun ComposeTestRule.verifyHomeScreenDisplayed() {
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
