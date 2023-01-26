package dev.zezula.books.data.model.shelf

import androidx.room.Entity

@Entity(tableName = "shelves")
data class Shelf(
    val id: String,
    val dateAdded: String,
    val title: String,
    val numberOfBooks: Int,
)

val previewShelves = listOf(
    Shelf(id = "1", dateAdded = "2023-01-05T17:43:25.629", title = "Favorites", numberOfBooks = 0),
    Shelf(id = "2", dateAdded = "2022-01-05T17:43:25.629", title = "Have Read", numberOfBooks = 0),
)
