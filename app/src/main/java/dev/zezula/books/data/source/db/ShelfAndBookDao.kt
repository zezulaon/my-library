package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

    @Upsert
    suspend fun addOrUpdate(shelf: ShelfEntity)

    @Upsert
    suspend fun addOrUpdate(shelves: List<ShelfEntity>)

    @Query("DELETE FROM shelves WHERE id = :shelfId")
    suspend fun delete(shelfId: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT *, Count(bookId) as numberOfBooks FROM shelves LEFT JOIN shelf_with_book ON shelfId=id GROUP BY id ORDER BY dateAdded DESC")
    fun getAllShelvesStream(): Flow<List<ShelfWithBookCountEntity>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM books INNER JOIN shelf_with_book ON bookId=id WHERE shelfId=:shelfId ORDER BY dateAdded DESC")
    fun getBooksForShelfStream(shelfId: String): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBookToShelf(shelvesWithBooksEntity: ShelfWithBookEntity)

    @Delete
    suspend fun removeBookFromShelf(shelvesWithBooksEntity: ShelfWithBookEntity)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT id, dateAdded, title, CASE WHEN bookId=:bookId THEN '1' ELSE '0' END AS isBookAdded FROM shelves LEFT JOIN (SELECT * FROM shelf_with_book WHERE bookId=:bookId) ON id=shelfId")
    fun getShelvesForBookStream(bookId: String): Flow<List<ShelfForBookEntity>>
}
