package dev.zezula.books.ui.screen.authors

import dev.zezula.books.data.model.book.Book

data class AuthorBooksUiState(
    val books: List<Book> = emptyList(),
    val authorName: String? = null,
    val errorMessage: Int? = null,
)
