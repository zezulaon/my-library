package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.data.model.book.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY dateAdded DESC")
    fun getAllBooksStream(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id=:bookId")
    fun getBookStream(bookId: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE isbn = :isbn ORDER BY dateAdded DESC")
    suspend fun getForIsbn(isbn: String): List<BookEntity>

    @Query("SELECT COUNT(id) FROM books")
    suspend fun getBookCount(): Int

    @Upsert
    suspend fun addOrUpdate(book: BookEntity)

    @Upsert
    suspend fun addOrUpdate(books: List<BookEntity>)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun delete(bookId: String)

    @Query("DELETE FROM books")
    suspend fun deleteAll()

    /**
     * Returns book for a given search query. Searches title and author columns.
     * @param query search query
     */
    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY dateAdded DESC")
    suspend fun getBooksForQuery(query: String): List<BookEntity>
}
