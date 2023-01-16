package dev.zezula.books.ui.screen.create

import dev.zezula.books.data.model.book.BookFormData

data class CreateBookUiState(
    val bookFormData: BookFormData = BookFormData(),
    val isInEditMode: Boolean = false,
    val isInProgress: Boolean = false,
    val isBookSaved: Boolean = false,
    val errorMessage: Int? = null,
    val invalidForm: Boolean = false,
)