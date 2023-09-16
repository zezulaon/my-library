package dev.zezula.books.ui.screen.search

data class FindBookUiState(
    val isInProgress: Boolean = false,
    val errorMessage: Int? = null,
    val noResultsMsgDisplayed: Boolean = false,
    val foundBooks: List<BookFormWithId> = emptyList(),
)
