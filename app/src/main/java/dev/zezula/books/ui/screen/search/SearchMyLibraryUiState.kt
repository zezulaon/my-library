package dev.zezula.books.ui.screen.search

import dev.zezula.books.core.model.Book

data class SearchMyLibraryUiState(
    val currentSearchQuery: String = "",
    val searchResults: List<Book> = emptyList(),
)
