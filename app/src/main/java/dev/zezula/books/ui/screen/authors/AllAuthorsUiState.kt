package dev.zezula.books.ui.screen.authors

import dev.zezula.books.core.model.AuthorAndBooks

data class AllAuthorsUiState(
    val authors: List<AuthorAndBooks> = emptyList(),
    val errorMessage: Int? = null,
)
