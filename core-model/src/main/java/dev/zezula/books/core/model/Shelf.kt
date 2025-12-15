package dev.zezula.books.core.model

data class Shelf(
    val id: Id,
    val dateAdded: String,
    val title: String,
    val numberOfBooks: Int,
) {
    @JvmInline
    value class Id(val value: String)
}

val previewShelves = listOf(
    Shelf(id = Shelf.Id("1"), dateAdded = "2023-01-05T17:43:25.629", title = "My Favorites", numberOfBooks = 0),
    Shelf(id = Shelf.Id("2"), dateAdded = "2022-01-05T17:43:25.629", title = "Wish List", numberOfBooks = 0),
)
