package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.data.model.shelf.ShelfEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {

    @Insert
    suspend fun insertShelf(shelf: ShelfEntity)

    @Upsert
    suspend fun insertOrUpdateShelf(shelf: ShelfEntity)

    @Query(
        """
        UPDATE shelves 
        SET title = :title, isPendingSync = 1
        WHERE id = :shelfId
        """,
    )
    suspend fun updateShelf(shelfId: String, title: String)

    @Query(
        """
        UPDATE shelves 
        SET isDeleted = 1, isPendingSync = 1
        WHERE id = :shelfId
        """,
    )
    suspend fun softDeleteShelf(shelfId: String)

    @Query(
        """
        UPDATE shelves  
        SET isPendingSync = 0 
        WHERE id = :shelfId
        """,
    )
    suspend fun resetShelfPendingSyncStatus(shelfId: String)

    @Query("SELECT * FROM shelves WHERE isPendingSync = 1")
    fun getAllShelvesPendingSyncShelvesStream(): Flow<List<ShelfEntity>>
}
