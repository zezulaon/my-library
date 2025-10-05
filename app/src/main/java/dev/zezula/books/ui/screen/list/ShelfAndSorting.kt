package dev.zezula.books.ui.screen.list

import dev.zezula.books.core.model.Shelf
import dev.zezula.books.domain.repositories.SortBooksBy

data class ShelfAndSorting(
    val shelf: Shelf?,
    val sortBooksBy: SortBooksBy,
)
