package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.tests.robot.DrawerItemType
import dev.zezula.books.tests.robot.onAllAuthorsScreen
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onAuthorBooksScreen
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.utils.TestBook
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class AuthorsInstrumentedTest : BaseInstrumentedTest() {

    private val addOrUpdateLibraryBookUseCase: AddOrUpdateLibraryBookUseCase by inject()

    private val bookToCreateFirst = TestBook(
        id = Book.Id("1"),
        title = "Two Towers",
        author = "J. R. R. Tolkien",
        description = "New desc",
        isbn = "987456",
        publisher = "newPublisher",
        yearPublished = 2000,
        pageCount = 300,
    )

    private val bookToCreateSecond = TestBook(
        id = Book.Id("2"),
        title = "The Fellowship of the Ring",
        author = "J. R. R. Tolkien",
        description = "New desc",
        isbn = "987457",
        publisher = "newPublisher",
        yearPublished = 2000,
        pageCount = 300,
    )

    private val bookToCreateThird = TestBook(
        id = Book.Id("3"),
        title = "Neverwhere",
        author = "Neil Gaiman",
        description = "New desc",
        isbn = "987458",
        publisher = "newPublisher",
        yearPublished = 2000,
        pageCount = 300,
    )

    @Before
    fun setup() = runBlocking {
        val testBooks = listOf(
            bookToCreateFirst,
            bookToCreateSecond,
            bookToCreateThird,
        )
        testBooks.forEach {
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
    fun when_there_are_books_by_specific_author_then_aggregated_data_in_all_authors_screen_matches() {
        onApp(composeTestRule) {
            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.AllAuthors)
            }

            onAllAuthorsScreen {
                assertToolbarAuthorSize(2)
                assertAuthorIsDisplayed("J. R. R. Tolkien")
                assertAuthorBookCountIsDisplayed(2)

                tapOnAuthor("J. R. R. Tolkien")
            }

            onAuthorBooksScreen {
                assertToolbarAuthorName("J. R. R. Tolkien")
                assertToolbarBookSize(2)

                tapOnBookTitle("Two Towers")
            }

            onBookDetailScreen {
                assertBookDisplayed(bookToCreateFirst)
            }
        }
    }
}
