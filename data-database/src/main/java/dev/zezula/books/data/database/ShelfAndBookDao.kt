package dev.zezula.books.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.data.database.entities.BookEntity
import dev.zezula.books.data.database.entities.ShelfForBookEntity
import dev.zezula.books.data.database.entities.ShelfWithBookCountEntity
import dev.zezula.books.data.database.entities.ShelfWithBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfAndBookDao {

    @Upsert
    suspend fun insertOrUpdateShelfWithBook(shelfWithBookEntity: ShelfWithBookEntity)

    @Upsert
    suspend fun insertOrUpdateShelvesWithBooks(shelfWithBookEntities: List<ShelfWithBookEntity>)

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
    fun getAllShelvesFlow(): Flow<List<ShelfWithBookCountEntity>>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM books     
        INNER JOIN shelf_with_book ON bookId=books.id 
        WHERE shelf_with_book.shelfId=:shelfId AND shelf_with_book.isDeleted = 0 AND books.isDeleted = 0
        ORDER BY books.dateAdded DESC
        """,
    )
    fun getAllBooksForShelfStream(shelfId: Shelf.Id): Flow<List<BookEntity>>

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
    fun getAllShelvesForBookFlow(bookId: Book.Id): Flow<List<ShelfForBookEntity>>

    @Query(
        """
        UPDATE shelf_with_book 
        SET isDeleted = 1, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE shelfId = :shelfId
        """,
    )
    suspend fun softDeleteShelvesWithBooksForShelf(shelfId: Shelf.Id, lastModifiedTimestamp: String)

    @Query(
        """
        UPDATE shelf_with_book 
        SET isDeleted = 1, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE bookId = :bookId
        """,
    )
    suspend fun softDeleteShelvesWithBooksForBook(bookId: Book.Id, lastModifiedTimestamp: String)

    @Query("SELECT * FROM shelf_with_book WHERE isPendingSync = 1")
    fun getAllShelvesWithBooksPendingSyncFlow(): Flow<List<ShelfWithBookEntity>>

    @Query("UPDATE shelf_with_book SET isPendingSync = 0 WHERE shelfId = :shelfId AND bookId = :bookId")
    suspend fun resetShelfWithBookPendingSyncStatus(shelfId: Shelf.Id, bookId: Book.Id)

    @Query("SELECT lastModifiedTimestamp FROM shelf_with_book ORDER BY lastModifiedTimestamp DESC LIMIT 1")
    suspend fun getLatestLastModifiedTimestamp(): String?
}
