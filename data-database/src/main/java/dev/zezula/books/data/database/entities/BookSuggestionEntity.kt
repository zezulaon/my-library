package dev.zezula.books.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import dev.zezula.books.core.model.Book

/**
 * Represents a record of a book that was suggested as a recommendation for another book (represented by
 * [parentBookId] ID).
 */
@Entity(
    tableName = "book_suggestions",
    primaryKeys = ["bookId", "parentBookId"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = CASCADE,
        ),
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentBookId"],
            onDelete = CASCADE,
        ),
    ],
)
data class BookSuggestionEntity(
    val bookId: Book.Id,
    val parentBookId: Book.Id,
)
