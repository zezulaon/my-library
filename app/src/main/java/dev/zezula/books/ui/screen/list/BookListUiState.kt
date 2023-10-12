package dev.zezula.books.ui.screen.list

import dev.zezula.books.data.SortBooksBy
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf

data class BookListUiState(
    val books: List<Book> = emptyList(),
    val drawerNavigation: DrawerNavigationState = DrawerNavigationState(),
    val sorting: SortingState = SortingState(),
    val infoMessages: InfoMessagesState = InfoMessagesState(),
    val selectedShelf: Shelf? = null,
)

data class DrawerNavigationState(
    val shelves: List<Shelf> = emptyList(),
    val managedShelvesClicked: Boolean = false,
    val allAuthorsClicked: Boolean = false,
)

data class SortingState(
    val sortDialogDisplayed: Boolean = false,
    val sortBooksBy: SortBooksBy = SortBooksBy.DATE_ADDED,
    val canSortByRating: Boolean = false,
)

data class InfoMessagesState(
    val addBookSheetOpened: Boolean = false,
    val moreDialogDisplayed: Boolean = false,
    val errorMessage: Int? = null,
)
