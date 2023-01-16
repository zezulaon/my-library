package dev.zezula.books.data.model.review

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.zezula.books.data.model.book.BookEntity

@Entity(
    tableName = "ratings",
    foreignKeys = [
        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = CASCADE),
    ],
    indices = [
        // Unique constraint - all [bookId] values in this table has to be unique
        Index(value = ["bookId"], unique = true)
    ]
)
data class RatingEntity(
    @PrimaryKey
    val id: String,
    val bookId: String,
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