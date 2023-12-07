package dev.zezula.books.data.source.db.legacy

import androidx.room.Dao
import androidx.room.Query
import dev.zezula.books.data.model.legacy.LegacyBookEntity
import dev.zezula.books.data.model.legacy.LegacyGroupShelfBookEntity
import dev.zezula.books.data.model.legacy.LegacyShelfEntity

@Dao
interface LegacyBookDao {

    @Query("SELECT * FROM volumes")
    suspend fun getAll(): List<LegacyBookEntity>

    @Query("SELECT * FROM bookshelves")
    suspend fun getAllShelves(): List<LegacyShelfEntity>

    @Query("SELECT * FROM group_volume_shelf")
    suspend fun getAllGroups(): List<LegacyGroupShelfBookEntity>
}
