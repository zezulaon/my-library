package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.core.model.previewBooks
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.tests.robot.AddBookOption
import dev.zezula.books.tests.robot.HomeCategory
import dev.zezula.books.tests.robot.InputType
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onBookEditorScreen
import dev.zezula.books.tests.robot.onHomeScreen
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class BookCrudInstrumentedTest : BaseInstrumentedTest() {

    private val addOrUpdateLibraryBookUseCase: AddOrUpdateLibraryBookUseCase by inject()

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
    }

    @Test
    fun when_book_is_added_manually_then_it_appears_in_the_library() {
        val bookToCreate = Book(
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

        composeTestRule.apply {
            onHomeScreen {
                tapOnAddBook(AddBookOption.MANUALLY)
            }

            onBookEditorScreen {
                typeInput(content = bookToCreate.title!!, type = InputType.TITLE)
                typeInput(content = bookToCreate.author!!, type = InputType.AUTHOR)
                typeInput(content = bookToCreate.publisher!!, type = InputType.PUBLISHER)
                typeInput(content = bookToCreate.yearPublished!!.toString(), type = InputType.YEAR)
                typeInput(content = bookToCreate.pageCount!!.toString(), type = InputType.PAGES)
                typeInput(content = bookToCreate.isbn!!, type = InputType.ISBN)
                typeInput(content = bookToCreate.description!!, type = InputType.DESC)

                tapOnSave()
            }

            onHomeScreen {
                assertCategoryDisplayed(HomeCategory.AllBooks)
                assertToolbarBookSize(testPreviewBooks.size + 1)

                tapOnBookTitle(bookToCreate.title!!)
            }

            onBookDetailScreen {
                assertBookDisplayed(bookToCreate)
            }
        }
    }

    @Test
    fun when_book_is_deleted_from_detail_then_it_disappears_from_library() {
        val bookToDelete = testPreviewBooks.first()

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
                assertToolbarBookSize(testPreviewBooks.size - 1)
                assertBookTitleDoesNotExist(bookToDelete.title!!)
            }
        }
    }
}
