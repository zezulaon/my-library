package dev.zezula.books.ui.screen.list

import dev.zezula.books.data.SortBooksBy
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf

data class BookListUiState(
    val books: List<Book> = emptyList(),
    val shelves: List<Shelf> = emptyList(),
    val errorMessage: Int? = null,
    val selectedShelf: Shelf? = null,
    val managedShelvesClicked: Boolean = false,
    val allAuthorsClicked: Boolean = false,
    val addBookSheetOpened: Boolean = false,
    val moreDialogDisplayed: Boolean = false,
    val sortDialogDisplayed: Boolean = false,
    val sortBooksBy: SortBooksBy = SortBooksBy.DATE_ADDED,
    val canSortByRating: Boolean = books.any { it.userRating != null },
)
