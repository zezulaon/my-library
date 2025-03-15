package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookSuggestionEntity
import dev.zezula.books.data.model.book.SearchBookResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    /**
     * Returns books that are part of user's personal book collection (library).
     */
    @RewriteQueriesToDropUnusedColumns // Removes unused [bookId] columns from the query.
    @Query(
        """
        SELECT * FROM books
        WHERE isInLibrary = 1 AND isDeleted = 0
        ORDER BY dateAdded DESC
        """,
    )
    fun getAllLibraryBooksStream(): Flow<List<BookEntity>>

    @RewriteQueriesToDropUnusedColumns // Removes unused [bookId] columns from the query.
    @Query("SELECT * FROM books WHERE books.isPendingSync = 1")
    fun getAllLibraryPendingSyncBooksStream(): Flow<List<BookEntity>>

    @Query("UPDATE books SET isPendingSync = 0 WHERE id = :bookId")
    suspend fun resetPendingSyncStatus(bookId: String)

    @Query("UPDATE books SET isPendingSync = 1 WHERE id = :bookId")
    suspend fun setPendingSyncStatus(bookId: String)

    /**
     * Checks if the book is part of the user's personal book collection (library).
     */
    @Query("SELECT EXISTS(SELECT 1 FROM books WHERE id = :bookId AND isInLibrary = 1)")
    fun isBookInLibrary(bookId: String): Flow<Boolean>

    /**
     * Flags the book as part of the user's personal book collection (library).
     */
    @Query(
        """
        UPDATE books 
        SET isInLibrary = 1, dateAdded = :dateAdded, isPendingSync = 1
        WHERE id = :bookId
        """,
    )
    suspend fun addToLibraryBooks(bookId: String, dateAdded: String)

    @Query(
        """
        UPDATE books 
        SET isDeleted = 1, isPendingSync = 1
        WHERE id = :bookId
        """,
    )
    suspend fun softDeleteBook(bookId: String)

    /**
     * For a given search query, returns books from user's personal library. Searches title and author columns.
     */
    @Query(
        """
            SELECT * FROM books
            WHERE 
                isInLibrary = 1 AND
                (title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%')
            ORDER BY dateAdded DESC""",
    )
    suspend fun getLibraryBooksForQuery(query: String): List<BookEntity>

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

    /**
     * Returns suggestions for a given book ID.
     */
    @Query("SELECT * FROM books INNER JOIN book_suggestions ON books.id = book_suggestions.bookId WHERE book_suggestions.parentBookId = :bookId")
    fun getSuggestionsForBook(bookId: String): Flow<List<BookEntity>>

    /**
     * Add the book to the "book_suggestions" reference table (Table with book suggestions).
     */
    @Upsert
    suspend fun addToBookSuggestions(bookSuggestionEntity: BookSuggestionEntity)

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

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun delete(bookId: String)

    @Query("DELETE FROM books")
    suspend fun deleteAll()

    @Query(
        """
        UPDATE books 
        SET thumbnailLink = :thumbnailLink, isPendingSync = 1
        WHERE id = :bookId
        """,
    )
    suspend fun updateBookCover(bookId: String, thumbnailLink: String)
}
