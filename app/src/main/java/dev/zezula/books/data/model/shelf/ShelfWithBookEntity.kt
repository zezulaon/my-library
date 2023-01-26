package dev.zezula.books.data.model.shelf

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
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
)
data class ShelfWithBookEntity(
    val bookId: String,
    val shelfId: String,
)

fun fromNetworkShelfWithBook(networkShelfWithBook: NetworkShelfWithBook): ShelfWithBookEntity {
    checkNotNull(networkShelfWithBook.shelfId) { "Entity needs [shelfId] property" }
    checkNotNull(networkShelfWithBook.bookId) { "Entity needs [bookId] property" }
    return ShelfWithBookEntity(
        bookId = networkShelfWithBook.bookId,
        shelfId = networkShelfWithBook.shelfId,
    )
}
