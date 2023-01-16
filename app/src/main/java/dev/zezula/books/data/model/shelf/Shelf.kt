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
    Shelf("1", "2015/15", "Favorites", 2),
    Shelf("2", "2015/15", "Have Read", 0),
)