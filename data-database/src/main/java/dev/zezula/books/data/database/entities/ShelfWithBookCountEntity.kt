package dev.zezula.books.data.database.entities

import dev.zezula.books.core.model.Shelf

data class ShelfWithBookCountEntity(
    val id: Shelf.Id,
    val dateAdded: String,
    val title: String,
    val numberOfBooks: Int,
)

fun ShelfWithBookCountEntity.asExternalModel(): Shelf {
    return Shelf(
        id = this.id,
        title = this.title,
        dateAdded = this.dateAdded,
        numberOfBooks = this.numberOfBooks,
    )
}
