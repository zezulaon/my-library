package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.tests.robot.AddBookOption
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onFindBookScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.utils.bookHobit
import dev.zezula.books.tests.utils.tapOnNavigateUp
import dev.zezula.books.tests.utils.testBooksData
import org.junit.Test

class FindBookInstrumentedTest : BaseInstrumentedTest() {

    @Test
    fun when_user_finds_book_online_then_it_can_be_added_to_library() {
        val bookHobit = testBooksData.bookHobit

        onApp(composeTestRule) {
            onHomeScreen {
                tapOnAddBook(AddBookOption.FIND_ONLINE)
            }

            onFindBookScreen {
                typeSearchQuery("tolkien")
                submitSearch()
                assertSearchResultIsDisplayed(bookHobit.title)
                tapOnSearchResult(bookHobit.title)
            }

            onBookDetailScreen {
                assertBookDisplayed(bookHobit)
                tapOnAddToLibrary()
                tapOnNavigateUp()
            }

            onFindBookScreen {
                tapOnNavigateUp()
            }

            onHomeScreen {
                assertBookTitleIsDisplayed(bookHobit.title)
            }
        }
    }
}
