package dev.zezula.books.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.zezula.books.core.model.Shelf

@Entity(tableName = "shelves")
data class ShelfEntity(
    @PrimaryKey
    val id: Shelf.Id,
    val dateAdded: String,
    val title: String,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isPendingSync: Boolean = false,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isDeleted: Boolean = false,
    val lastModifiedTimestamp: String? = null,
)

val previewShelfEntities = listOf(
    ShelfEntity(id = Shelf.Id("1"), dateAdded = "2023-01-05T17:43:25.629", title = "Favorites", lastModifiedTimestamp = "2023-01-05T17:43:25.629"),
    ShelfEntity(id = Shelf.Id("2"), dateAdded = "2022-01-05T17:43:25.629", title = "Have Read", lastModifiedTimestamp = "2022-01-05T17:43:25.629"),
)
