package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.data.model.shelf.ShelfEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {

    @Insert
    suspend fun insertShelf(shelf: ShelfEntity)

    @Upsert
    suspend fun insertOrUpdateShelf(shelf: ShelfEntity)

    @Upsert
    suspend fun insertOrUpdateShelves(shelfEntities: List<ShelfEntity>)

    @Query(
        """
        UPDATE shelves 
        SET title = :title, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE id = :shelfId
        """,
    )
    suspend fun updateShelf(shelfId: Shelf.Id, title: String, lastModifiedTimestamp: String)

    @Query(
        """
        UPDATE shelves 
        SET isDeleted = 1, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE id = :shelfId
        """,
    )
    suspend fun softDeleteShelf(shelfId: Shelf.Id, lastModifiedTimestamp: String)

    @Query(
        """
        UPDATE shelves  
        SET isPendingSync = 0 
        WHERE id = :shelfId
        """,
    )
    suspend fun resetShelfPendingSyncStatus(shelfId: Shelf.Id)

    @Query("SELECT * FROM shelves WHERE isPendingSync = 1")
    fun getAllShelvesPendingSyncStream(): Flow<List<ShelfEntity>>

    @Query("SELECT lastModifiedTimestamp FROM shelves ORDER BY lastModifiedTimestamp DESC LIMIT 1")
    suspend fun getLatestLastModifiedTimestamp(): String?
}
