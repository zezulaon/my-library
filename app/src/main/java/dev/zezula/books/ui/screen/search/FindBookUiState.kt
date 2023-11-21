package dev.zezula.books.ui.screen.search

import dev.zezula.books.data.model.book.Book

data class FindBookUiState(
    val isInProgress: Boolean = false,
    val errorMessage: Int? = null,
    val noResultsMsgDisplayed: Boolean = false,
    val foundBooks: List<Book> = emptyList(),
)
