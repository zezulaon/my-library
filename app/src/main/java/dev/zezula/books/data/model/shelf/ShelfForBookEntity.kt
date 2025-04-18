package dev.zezula.books.data.model.shelf

data class ShelfForBookEntity(
    val id: Shelf.Id,
    val title: String,
    val isBookAdded: Boolean,
)

fun ShelfForBookEntity.asExternalModel(): ShelfForBook {
    return ShelfForBook(
        id = id,
        title = title,
        isBookAdded = isBookAdded,
    )
}
