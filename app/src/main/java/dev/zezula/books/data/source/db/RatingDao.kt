package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
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
