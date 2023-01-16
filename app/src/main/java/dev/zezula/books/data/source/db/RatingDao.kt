package dev.zezula.books.data.source.db

import androidx.room.*
import dev.zezula.books.data.model.*
import dev.zezula.books.data.model.review.RatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {

    @Query("SELECT * FROM ratings WHERE bookId = :bookId")
    fun getRating(bookId: String): Flow<RatingEntity?>

    @Upsert
    suspend fun addRating(rating: RatingEntity)

    @Query("DELETE FROM ratings WHERE bookId = :bookId")
    suspend fun deleteAllForBookId(bookId: String)
}