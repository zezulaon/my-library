package dev.zezula.books.data.model.shelf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelves")
data class ShelfEntity(
    @PrimaryKey
    val id: String,
    val dateAdded: String,
    val title: String,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isPendingSync: Boolean = false,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isDeleted: Boolean = false,
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

val previewShelfEntities = listOf(
    ShelfEntity(id = "1", dateAdded = "2023-01-05T17:43:25.629", title = "Favorites"),
    ShelfEntity(id = "2", dateAdded = "2022-01-05T17:43:25.629", title = "Have Read"),
)
