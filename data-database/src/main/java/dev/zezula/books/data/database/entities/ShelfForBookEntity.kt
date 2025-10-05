package dev.zezula.books.data.database.entities

import dev.zezula.books.core.model.Shelf
import dev.zezula.books.core.model.ShelfForBook

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
