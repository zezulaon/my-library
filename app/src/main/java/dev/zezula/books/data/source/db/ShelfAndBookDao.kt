package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfForBookEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookCountEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfAndBookDao {

    @Insert
    suspend fun insertShelf(shelf: ShelfEntity)

    @Query(
        """
        UPDATE shelves 
        SET title = :title, isPendingSync = 1
        WHERE id = :shelfId
        """,
    )
    suspend fun updateShelf(shelfId: String, title: String)

    @Upsert
    suspend fun addOrUpdate(shelves: List<ShelfEntity>)

    @Query("DELETE FROM shelves WHERE id = :shelfId")
    suspend fun delete(shelfId: String)

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
        UPDATE shelf_with_book 
        SET isDeleted = 1, isPendingSync = 1 
        WHERE shelfId = :shelfId
        """,
    )
    suspend fun softDeleteShelvesWithBooksForShelf(shelfId: String)

    @Query(
        """
        UPDATE shelf_with_book 
        SET isDeleted = 1, isPendingSync = 1
        WHERE bookId = :bookId
        """,
    )
    suspend fun softDeleteShelvesWithBooksForBook(bookId: String)

    @Query("UPDATE shelves SET isPendingSync = 1 WHERE id = :shelfId")
    suspend fun setPendingSyncStatus(shelfId: String)

    @Query("UPDATE shelves SET isPendingSync = 0 WHERE id = :shelfId")
    suspend fun resetPendingSyncStatus(shelfId: String)

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT *, Count(bookId) as numberOfBooks 
        FROM shelves 
        LEFT JOIN shelf_with_book ON shelfId=id AND shelf_with_book.isDeleted = 0 
        WHERE shelves.isDeleted = 0 AND (shelf_with_book.isDeleted = 0 OR shelf_with_book.isDeleted IS NULL)
        GROUP BY id 
        ORDER BY dateAdded DESC
        """,
    )
    fun getAllShelvesStream(): Flow<List<ShelfWithBookCountEntity>>

    @Query("SELECT * FROM shelves WHERE isPendingSync = 1")
    fun getAllPendingSyncShelvesStream(): Flow<List<ShelfEntity>>

    @Query("SELECT * FROM shelf_with_book WHERE isPendingSync = 1")
    fun getAllShelvesWithBooksPendingSyncFlow(): Flow<List<ShelfWithBookEntity>>

    @Query("UPDATE shelf_with_book SET isPendingSync = 0 WHERE shelfId = :shelfId AND bookId = :bookId")
    suspend fun resetShelvesWithBooksPendingSyncStatus(shelfId: String, bookId: String)

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM books     
        INNER JOIN shelf_with_book ON bookId=books.id 
        WHERE shelf_with_book.shelfId=:shelfId AND shelf_with_book.isDeleted = 0 AND books.isDeleted = 0
        ORDER BY books.dateAdded DESC
        """,
    )
    fun getAllBooksForShelfStream(shelfId: String): Flow<List<BookEntity>>

    @Upsert
    suspend fun addBookToShelf(shelvesWithBooksEntity: ShelfWithBookEntity)

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT id, dateAdded, title, 
            CASE WHEN bookId=:bookId THEN '1' ELSE '0' END AS isBookAdded 
        FROM shelves 
        LEFT JOIN 
            (SELECT * FROM shelf_with_book WHERE shelf_with_book.isDeleted = 0 AND bookId=:bookId)
            ON id=shelfId
        WHERE shelves.isDeleted = 0
        """,
    )
    fun getShelvesForBookStream(bookId: String): Flow<List<ShelfForBookEntity>>
}
