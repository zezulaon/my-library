package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.tests.robot.AddBookOption
import dev.zezula.books.tests.robot.HomeCategory
import dev.zezula.books.tests.robot.InputType
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onBookEditorScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.utils.bookHobit
import dev.zezula.books.tests.utils.tapOnNavigateUp
import dev.zezula.books.tests.utils.testBooksData
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class BooksManagementInstrumentedTest : BaseInstrumentedTest() {

    private val addOrUpdateLibraryBookUseCase: AddOrUpdateLibraryBookUseCase by inject()

    private val bookToCreate = Book(
        id = Book.Id("123"),
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
                    thumbnailLink = it.thumbnailLink,
                ),
            )
        }
    }

    @Test
    fun when_book_is_added_manually_then_it_appears_in_the_library() {
        composeTestRule.apply {
            onHomeScreen {
                tapOnAddBook(AddBookOption.MANUALLY)
            }

            onBookEditorScreen {
                typeInput(text = bookToCreate.title!!, type = InputType.TITLE)
                typeInput(text = bookToCreate.author!!, type = InputType.AUTHOR)
                typeInput(text = bookToCreate.publisher!!, type = InputType.PUBLISHER)
                typeInput(text = bookToCreate.yearPublished!!.toString(), type = InputType.YEAR)
                typeInput(text = bookToCreate.pageCount!!.toString(), type = InputType.PAGES)
                typeInput(text = bookToCreate.isbn!!, type = InputType.ISBN)
                typeInput(text = bookToCreate.description!!, type = InputType.DESC)

                tapOnSave()
            }

            onHomeScreen {
                assertCategoryDisplayed(HomeCategory.AllBooks)
                assertToolbarBookSize(testBooksData.size + 1)

                tapOnBookTitle(bookToCreate.title!!)
            }

            onBookDetailScreen {
                assertBookDisplayed(bookToCreate)
            }
        }
    }

    @Test
    fun when_book_is_deleted_from_detail_then_it_disappears_from_library() {
        val bookToDelete = testBooksData.bookHobit

        composeTestRule.apply {
            onHomeScreen {
                tapOnBookTitle(bookToDelete.title!!)
            }

            onBookDetailScreen {
                assertBookDisplayed(bookToDelete)
                tapOnDeleteButton()
                confirmDeletion()
            }

            onHomeScreen {
                assertCategoryDisplayed(HomeCategory.AllBooks)
                assertToolbarBookSize(testBooksData.size - 1)
                assertBookTitleDoesNotExist(bookToDelete.title!!)
            }
        }
    }

    @Test
    fun when_book_is_updated_from_detail_then_changes_are_reflected_in_library() {
        val bookToUpdate = testBooksData.bookHobit

        composeTestRule.apply {
            onHomeScreen {
                tapOnBookTitle(bookToUpdate.title!!)
            }

            onBookDetailScreen {
                tapOnEditButton()
            }

            onBookEditorScreen {
                typeInput(text = bookToCreate.title!!, type = InputType.TITLE, clearExistingText = true)
                typeInput(text = bookToCreate.author!!, type = InputType.AUTHOR, clearExistingText = true)
                typeInput(text = bookToCreate.publisher!!, type = InputType.PUBLISHER, clearExistingText = true)
                typeInput(text = bookToCreate.yearPublished!!.toString(), type = InputType.YEAR, clearExistingText = true)
                typeInput(text = bookToCreate.pageCount!!.toString(), type = InputType.PAGES, clearExistingText = true)
                typeInput(text = bookToCreate.isbn!!, type = InputType.ISBN, clearExistingText = true)
                typeInput(text = bookToCreate.description!!, type = InputType.DESC, clearExistingText = true)
                tapOnSave()
            }

            onBookDetailScreen {
                assertBookDisplayed(bookToCreate)
                tapOnNavigateUp()
            }

            onHomeScreen {
                assertToolbarBookSize(testBooksData.size)
                assertBookTitleDoesNotExist(bookToUpdate.title!!)
                assertBookTitleIsDisplayed(bookToCreate.title!!)
            }
        }
    }

    @Test
    fun when_user_confirms_empty_book_form_then_error_is_shown() {
        composeTestRule.apply {
            onHomeScreen {
                tapOnAddBook(AddBookOption.MANUALLY)
            }

            onBookEditorScreen {
                tapOnSave()
                assertInvalidInputErrorDisplayed()
            }
        }
    }
}
