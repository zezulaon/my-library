package dev.zezula.books.ui.screen.search

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Shelf

data class SearchBarcodeUiState(
    val scannedIsbn: String? = null,
    val isSearchInProgress: Boolean = false,
    val selectedShelf: Shelf? = null,
    val noBookFound: Boolean = false,
    val errorMessage: Int? = null,
    val foundedBookId: Book.Id? = null,
    val foundedBook: Book? = null,
)
