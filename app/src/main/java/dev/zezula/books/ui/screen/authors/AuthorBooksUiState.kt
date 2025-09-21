package dev.zezula.books.ui.screen.authors

import dev.zezula.books.core.model.Book

data class AuthorBooksUiState(
    val books: List<Book> = emptyList(),
    val authorName: String? = null,
    val errorMessage: Int? = null,
)
