package dev.zezula.books.core.model

data class ShelfForBook(
    val id: Shelf.Id,
    val title: String,
    val isBookAdded: Boolean,
)

val previewShelvesForBook = previewShelves.map { shelf ->
    ShelfForBook(id = shelf.id, title = shelf.title, isBookAdded = true)
}
