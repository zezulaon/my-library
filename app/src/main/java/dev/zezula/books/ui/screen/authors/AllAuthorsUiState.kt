package dev.zezula.books.ui.screen.authors

data class AllAuthorsUiState(
    val authors: List<AuthorAndBooks> = emptyList(),
    val errorMessage: Int? = null,
)
