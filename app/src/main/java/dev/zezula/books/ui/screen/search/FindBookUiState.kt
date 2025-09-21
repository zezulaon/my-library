package dev.zezula.books.ui.screen.search

import dev.zezula.books.core.model.Book

data class FindBookUiState(
    val isInProgress: Boolean = false,
    val errorMessage: Int? = null,
    val noResultsMsgDisplayed: Boolean = false,
    val foundBooks: List<Book> = emptyList(),
)
