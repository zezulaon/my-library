package dev.zezula.books.data.model.review

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = CASCADE),
    ],
    indices = [
        // Index on [bookId] column is used to improve the performance of queries that filter by [bookId]
        Index(value = ["bookId"]),
    ],
)
data class ReviewEntity(
    @PrimaryKey
    val id: Review.Id,
    val bookId: Book.Id,
    val body: String? = null,
    val link: String? = null,
    val rating: Int? = null,
    val votes: Int? = null,
    val spoilerFlag: Boolean = false,
    val userName: String? = null,
    val userImageLink: String? = null,
)

fun ReviewEntity.asExternalModel() =
    Review(
        id = this.id,
        body = this.body,
        link = this.link,
        rating = this.rating,
        votes = this.votes,
        spoilerFlag = this.spoilerFlag,
        userName = this.userName,
        userImageLink = this.userImageLink,
    )
