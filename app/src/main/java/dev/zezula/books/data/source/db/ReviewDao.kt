package dev.zezula.books.data.source.db

import androidx.room.*
import dev.zezula.books.data.model.*
import dev.zezula.books.data.model.review.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE bookId = :bookId")
    fun getReviews(bookId: String): Flow<List<ReviewEntity>?>

    @Upsert
    suspend fun addReviews(reviews: List<ReviewEntity>)

    @Query("DELETE FROM reviews WHERE bookId = :bookId")
    suspend fun deleteAllForBookId(bookId: String)
}