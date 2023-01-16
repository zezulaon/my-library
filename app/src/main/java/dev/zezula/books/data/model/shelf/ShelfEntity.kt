package dev.zezula.books.data.model.shelf

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelves")
data class ShelfEntity(
    @PrimaryKey
    val id: String,
    val dateAdded: String,
    val title: String,
)

fun fromNetworkShelf(networkShelf: NetworkShelf): ShelfEntity {
    checkNotNull(networkShelf.id) { "Book needs [id] property" }
    checkNotNull(networkShelf.title) { "Book needs [title] property" }
    checkNotNull(networkShelf.dateAdded) { "Book needs [dateAdded] property" }
    return ShelfEntity(
        id = networkShelf.id,
        dateAdded = networkShelf.dateAdded,
        title = networkShelf.title,
    )
}