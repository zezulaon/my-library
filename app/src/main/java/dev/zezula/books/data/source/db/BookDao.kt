package dev.zezula.books.data.source.db

import androidx.room.*
import dev.zezula.books.data.model.book.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY dateAdded DESC")
    fun getAllBooksAsStream(): Flow<List<BookEntity>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM books INNER JOIN shelf_with_book ON bookId=id WHERE shelfId=:shelfId ORDER BY dateAdded DESC")
    fun getForShelfAsStream(shelfId: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id=:bookId")
    fun getBook(bookId: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE isbn = :isbn ORDER BY dateAdded DESC")
    suspend fun getForIsbn(isbn: String): List<BookEntity>

    @Query("SELECT COUNT(id) FROM books")
    suspend fun getBookCount(): Int

    @Upsert
    suspend fun addOrUpdate(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun delete(bookId: String)

    @Query("DELETE FROM books")
    suspend fun deleteAll()
}