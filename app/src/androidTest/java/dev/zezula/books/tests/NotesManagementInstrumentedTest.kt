package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.tests.robot.BookDetailTab
import dev.zezula.books.tests.robot.DrawerItemType
import dev.zezula.books.tests.robot.onAllNotesScreen
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onBookDetailScreen
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onNotesTab
import dev.zezula.books.tests.utils.bookHobit
import dev.zezula.books.tests.utils.tapOnNavigateUp
import dev.zezula.books.tests.utils.testBooksData
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class NotesManagementInstrumentedTest : BaseInstrumentedTest() {

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
    fun when_note_is_added_and_edited_and_deleted_then_changes_are_reflected_in_all_notes_screen() {
        val book = testBooksData.bookHobit
        val noteText = "This is a test note for Hobit."
        val updatedNoteText = "This is an updated test note for Hobit."

        onApp(composeTestRule) {
            onHomeScreen {
                tapOnBookTitle(book.title)
            }

            onBookDetailScreen {
                tapOnTab(BookDetailTab.NOTES)
                onNotesTab {
                    addNote(noteText)
                    assertNoteDisplayed(noteText)
                }
                tapOnNavigateUp()
            }

            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.AllNotes)
            }

            onAllNotesScreen {
                assertNumberOfNotes(1)
                assertNoteDisplayed(text = noteText, bookTitle = book.title)
                tapOnNavigateUp()
            }

            // Edit Note
            onHomeScreen {
                tapOnBookTitle(book.title)
            }

            onBookDetailScreen {
                tapOnTab(BookDetailTab.NOTES)
                onNotesTab {
                    tapOnEditNote(noteText)
                    updateNoteText(updatedNoteText)
                    tapOnUpdateNote()
                    assertNoteDisplayed(updatedNoteText)
                }
                tapOnNavigateUp()
            }

            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.AllNotes)
            }

            onAllNotesScreen {
                assertNumberOfNotes(1)
                assertNoteDisplayed(text = updatedNoteText, bookTitle = book.title)
                tapOnNote(updatedNoteText)
            }

            // Delete Note
            onBookDetailScreen {
                tapOnTab(BookDetailTab.NOTES)
                onNotesTab {
                    tapOnDeleteNote(updatedNoteText)
                    assertNoteDoesNotExist(updatedNoteText)
                }
                tapOnNavigateUp()
            }

            onAllNotesScreen {
                assertNumberOfNotes(0)
                assertNoteDoesNotExist(updatedNoteText)
            }
        }
    }

    @Test
    fun when_book_is_deleted_then_associated_notes_are_deleted() {
        val book = testBooksData.bookHobit
        val firstNoteText = "This is a note for the book that will be deleted."
        val secondNoteText = "This is another note for the book that will be deleted."

        onApp(composeTestRule) {
            onHomeScreen {
                tapOnBookTitle(book.title)
            }

            onBookDetailScreen {
                tapOnTab(BookDetailTab.NOTES)
                onNotesTab {
                    addNote(firstNoteText)
                    addNote(secondNoteText)
                }
                tapOnNavigateUp()
            }

            onHomeScreen {
                openNavigationDrawer()
                tapOnNavigationDrawerItem(DrawerItemType.AllNotes)
            }

            onAllNotesScreen {
                assertNumberOfNotes(2)
                assertNoteDisplayed(text = firstNoteText, bookTitle = book.title)
                assertNoteDisplayed(text = secondNoteText, bookTitle = book.title)
                tapOnNote(secondNoteText)
            }

            // Delete Book
            onBookDetailScreen {
                tapOnTab(BookDetailTab.DETAIL)
                tapOnDeleteButton()
                confirmDeletion()
            }

            onAllNotesScreen {
                assertNumberOfNotes(0)
                assertNoteDoesNotExist(firstNoteText)
            }
        }
    }
}
