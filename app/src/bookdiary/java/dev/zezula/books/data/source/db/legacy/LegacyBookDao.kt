package dev.zezula.books.data.source.db.legacy

import androidx.room.Dao
import androidx.room.Query
import dev.zezula.books.data.model.legacy.LegacyBookEntity
import dev.zezula.books.data.model.legacy.LegacyGroupShelfBookEntity
import dev.zezula.books.data.model.legacy.LegacyShelfEntity
import dev.zezula.books.data.model.legacy.LegacyStateEntity

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
