package dev.zezula.books.ui.screen.search

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf

data class SearchBarcodeUiState(
    val scannedIsbn: String? = null,
    val isSearchInProgress: Boolean = false,
    val selectedShelf: Shelf? = null,
    val noBookFound: Boolean = false,
    val errorMessage: Int? = null,
    val foundedBookId: String? = null,
    val foundedBook: Book? = null,
)
