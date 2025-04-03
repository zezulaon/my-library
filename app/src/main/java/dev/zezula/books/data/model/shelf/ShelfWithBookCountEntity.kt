package dev.zezula.books.data.model.shelf

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
