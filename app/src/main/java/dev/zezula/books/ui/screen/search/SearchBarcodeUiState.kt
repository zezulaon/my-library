package dev.zezula.books.ui.screen.search

data class SearchBarcodeUiState(
    val barcode: String = "",
    val isInProgress: Boolean = false,
    val noBookFound: Boolean = false,
    val errorMessage: Int? = null,
    val foundBookId: String? = null,
)