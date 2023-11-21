package dev.zezula.books.data.model.book

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

/**
 * Represents a record of a book from search results. This is used to track which books have been found online and are
 * stored temporarily in the database - these books are deleted next time the user searches for a book (unless the user
 * adds them to their library).
 *
 */
@Entity(
    tableName = "search_book_results",
    foreignKeys = [
        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = CASCADE),
    ],
)
data class SearchBookResultEntity(
    @PrimaryKey
    val bookId: String,
)
