package dev.zezula.books.ui.screen.search

import dev.zezula.books.data.model.book.Book

data class SearchMyLibraryUiState(
    val currentSearchQuery: String = "",
    val searchResults: List<Book> = emptyList(),
)
