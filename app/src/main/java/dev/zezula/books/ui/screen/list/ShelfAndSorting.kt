package dev.zezula.books.ui.screen.list

import dev.zezula.books.data.SortBooksBy
import dev.zezula.books.data.model.shelf.Shelf

data class ShelfAndSorting(
    val shelf: Shelf?,
    val sortBooksBy: SortBooksBy,
)
