package dev.zezula.books.data.model.shelf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import dev.zezula.books.data.model.book.BookEntity

// https://sqlite.org/foreignkeys.html
// "onDelete = CASCADE" ensures that when deleting a shelf, all shelf related records in [shelves_with_books] table
// are deleted as well (and same for books).
@Entity(
    tableName = "shelf_with_book",
    primaryKeys = ["bookId", "shelfId"],
    foreignKeys = [
        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = CASCADE),
        ForeignKey(entity = ShelfEntity::class, parentColumns = ["id"], childColumns = ["shelfId"], onDelete = CASCADE),
    ],
    indices = [
        // Index on [bookId] and [shelfId] column is used to improve the performance of queries that use these columns.
        Index(value = ["shelfId"]),
        Index(value = ["bookId"]),
    ],
)
data class ShelfWithBookEntity(
    val bookId: String,
    val shelfId: String,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isPendingSync: Boolean = false,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isDeleted: Boolean = false,
    val lastModifiedTimestamp: String? = null,
)

fun ShelfWithBookEntity.asNetworkShelfWithBook(): NetworkShelfWithBook {
    return NetworkShelfWithBook(
        bookId = bookId,
        shelfId = shelfId,
        isDeleted = isDeleted,
        lastModifiedTimestamp = lastModifiedTimestamp,
    )
}