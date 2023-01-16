package dev.zezula.books.data.source.db

import androidx.room.*
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfForBookEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookCountEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT *, Count(bookId) as numberOfBooks FROM shelves LEFT JOIN shelf_with_book ON shelfId=id GROUP BY id ORDER BY dateAdded DESC")
    fun getAllAsStream(): Flow<List<ShelfWithBookCountEntity>>

    @Upsert
    suspend fun addOrUpdate(shelf: ShelfEntity)

    @Query("DELETE FROM shelves WHERE id = :shelfId")
    suspend fun delete(shelfId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBookToShelf(shelvesWithBooksEntity: ShelfWithBookEntity)

    @Delete
    suspend fun removeBookFromShelf(shelvesWithBooksEntity: ShelfWithBookEntity)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT id, dateAdded, title, CASE WHEN bookId=:bookId THEN '1' ELSE '0' END AS isBookAdded FROM shelves LEFT JOIN (SELECT * FROM shelf_with_book WHERE bookId=:bookId) ON id=shelfId")
    fun getShelvesForBookAsStream(bookId: String): Flow<List<ShelfForBookEntity>>
}