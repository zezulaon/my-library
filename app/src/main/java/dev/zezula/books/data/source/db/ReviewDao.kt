package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.review.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE bookId = :bookId")
    fun getReviewsForBookFlow(bookId: Book.Id): Flow<List<ReviewEntity>>

    @Upsert
    suspend fun addReviews(reviews: List<ReviewEntity>)

    @Query("DELETE FROM reviews WHERE bookId = :bookId")
    suspend fun deleteAllForBookId(bookId: Book.Id)
}
