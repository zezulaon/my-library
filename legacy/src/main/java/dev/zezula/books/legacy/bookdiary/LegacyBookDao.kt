package dev.zezula.books.legacy.bookdiary

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LegacyBookDao {

    @Query("SELECT * FROM books")
    suspend fun getAll(): List<LegacyBookEntity>?

    @Query("SELECT * FROM states WHERE bookId = :bookId")
    suspend fun getStatesForBookId(bookId: Int): List<LegacyStateEntity>?

    @Query("SELECT * FROM groups")
    suspend fun getAllShelves(): List<LegacyShelfEntity>

    @Query("SELECT * FROM bookGroup")
    suspend fun getAllGroups(): List<LegacyGroupShelfBookEntity>
}
