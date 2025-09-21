package dev.zezula.books.ui.screen.shelves

import dev.zezula.books.core.model.Shelf

data class ShelvesUiState(
    val shelves: List<Shelf> = emptyList(),
    val showAddOrEditShelfDialog: Boolean = false,
    val selectedShelf: Shelf? = null,
    val errorMessage: Int? = null,
)
