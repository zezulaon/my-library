package dev.zezula.books.legacy.gb

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LegacyBookDao {

    @Query("SELECT * FROM volumes")
    suspend fun getAll(): List<LegacyBookEntity>

    @Query("SELECT * FROM bookshelves")
    suspend fun getAllShelves(): List<LegacyShelfEntity>

    @Query("SELECT * FROM group_volume_shelf")
    suspend fun getAllGroups(): List<LegacyGroupShelfBookEntity>
}