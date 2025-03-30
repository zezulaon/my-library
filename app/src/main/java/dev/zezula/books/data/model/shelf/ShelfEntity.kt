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
    val lastModifiedTimestamp: String? = null,
)

fun ShelfEntity.asNetworkShelf(): NetworkShelf {
    return NetworkShelf(
        id = id,
        dateAdded = dateAdded,
        title = title,
        isDeleted = isDeleted,
        lastModifiedTimestamp = lastModifiedTimestamp,
    )
}

val previewShelfEntities = listOf(
    ShelfEntity(id = "1", dateAdded = "2023-01-05T17:43:25.629", title = "Favorites", lastModifiedTimestamp = "2023-01-05T17:43:25.629"),
    ShelfEntity(id = "2", dateAdded = "2022-01-05T17:43:25.629", title = "Have Read", lastModifiedTimestamp = "2022-01-05T17:43:25.629"),
)
