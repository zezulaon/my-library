package dev.zezula.books.data.model.review

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity

@Entity(
    tableName = "ratings",
    foreignKeys = [
        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = CASCADE),
    ],
    indices = [
        // Index on [bookId] column is used to improve the performance of queries that filter by [bookId]
        // Unique constraint - all [bookId] values in this table has to be unique (a book can have only one rating)
        Index(value = ["bookId"], unique = true),
    ],
)
data class RatingEntity(
    // TODO: Primary [id] key and foreign [bookId] key could be combined into one property since book can have exactly
    //  one rating
    @PrimaryKey
    val id: Rating.Id,
    val bookId: Book.Id,
    val averageRating: String? = null,
    val textReviewsCount: Int? = null,
    val ratingsCount: Int? = null,
)

fun RatingEntity.asExternalModel() =
    Rating(
        id = this.id,
        bookId = this.bookId,
        averageRating = this.averageRating,
        textReviewsCount = this.textReviewsCount,
        ratingsCount = this.ratingsCount,
    )
