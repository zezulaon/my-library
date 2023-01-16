package dev.zezula.books.data.model.shelf

data class ShelfForBook(
    val id: String,
    val title: String,
    val isBookAdded: Boolean,
)

val previewShelvesForBook = previewShelves.map { shelf ->
    ShelfForBook(id = shelf.id, title = shelf.title, isBookAdded = true)
}
