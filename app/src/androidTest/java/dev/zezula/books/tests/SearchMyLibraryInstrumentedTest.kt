package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onSearchMyLibraryScreen
import dev.zezula.books.tests.utils.bookHobit
import dev.zezula.books.tests.utils.testBooksData
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class SearchMyLibraryInstrumentedTest : BaseInstrumentedTest() {

    private val addOrUpdateLibraryBookUseCase: AddOrUpdateLibraryBookUseCase by inject()

    @Before
    fun setup() = runBlocking {
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
                ),
            )
        }
    }

    @Test
    fun when_user_searches_for_book_then_results_are_displayed_and_can_be_navigated() {
        val bookHobit = testBooksData.bookHobit
        // "Neverwhere" is another test book we want to ensure doesn't show up.
        val bookNeverwhere = testBooksData.first { it.title == "Neverwhere" }

        onApp(composeTestRule) {
            onHomeScreen {
                navigateToSearch()
            }

            onSearchMyLibraryScreen {
                typeSearchQuery("hobit")
                assertSearchResultIsDisplayed(bookHobit.title)
                assertSearchResultDoesNotExist(bookNeverwhere.title)
                tapOnSearchResult(bookHobit.title)
            }

            onBookDetailScreen {
                assertBookDisplayed(bookHobit)
            }
        }
    }

    @Test
    fun when_user_searches_for_author_then_results_are_displayed_and_can_be_navigated() {
        val bookHobit = testBooksData.bookHobit
        val bookNeverwhere = testBooksData.first { it.title == "Neverwhere" }

        onApp(composeTestRule) {
            onHomeScreen {
                navigateToSearch()
            }

            onSearchMyLibraryScreen {
                typeSearchQuery("gaim")
                assertSearchResultIsDisplayed(bookNeverwhere.title)
                assertSearchResultDoesNotExist(bookHobit.title)
                tapOnSearchResult(bookNeverwhere.title)
            }

            onBookDetailScreen {
                assertBookDisplayed(bookNeverwhere)
            }
        }
    }
}
