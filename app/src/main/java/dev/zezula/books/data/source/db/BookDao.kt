package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.LibraryBookEntity
import dev.zezula.books.data.model.book.SearchBookResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    /**
     * Returns books that are also added in "library_books" reference table.
     */
    @RewriteQueriesToDropUnusedColumns // Removes unused [bookId] columns from the query.
    @Query("SELECT * FROM books INNER JOIN library_books ON library_books.bookId = books.id ORDER BY dateAdded DESC")
    fun getAllLibraryBooksStream(): Flow<List<BookEntity>>

    /**
     * Returns book that is also added in "library_books" reference table.
     */
    @RewriteQueriesToDropUnusedColumns // Removes unused [bookId] columns from the query.
    @Query("SELECT * FROM books INNER JOIN library_books ON books.id = library_books.bookId WHERE books.id = :bookId")
    fun getLibraryBookStream(bookId: String): Flow<LibraryBookEntity?>

    /**
     * Add the book to the "library_books" reference table (Table with user's personal book collection).
     */
    @Upsert
    suspend fun addToLibraryBooks(libraryBookEntity: LibraryBookEntity)

    /**
     * For a given search query, returns books from "library_books" reference table (Table with user's personal book
     * collection). Searches title and author columns.
     */
    @Query(
        "SELECT * FROM books INNER JOIN library_books ON library_books.bookId = books.id WHERE " +
            "title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY dateAdded DESC",
    )
    suspend fun getLibraryBooksForForQuery(query: String): List<BookEntity>

    /**
     * Returns search results - books that were found online and are stored temporarily in the database.
     */
    @RewriteQueriesToDropUnusedColumns // Removes unused [bookId] columns from the query.
    @Query("SELECT * FROM books INNER JOIN search_book_results ON books.id = search_book_results.bookId")
    fun getAllSearchResultBooksStream(): Flow<List<BookEntity>>

    /**
     * Add the book to the "search_book_results" reference table (Table with temporary search results).
     */
    @Upsert
    suspend fun addToSearchBookResults(searchBookResultEntity: SearchBookResultEntity)

    /**
     * Delete all books from the "search_book_results" reference table.
     */
    @Query("DELETE FROM search_book_results")
    suspend fun deleteAllSearchBookResults()

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
}
