package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
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

    /**
     * For a given search query, returns books from user's personal library. Searches title and author columns.
     */
    @Query(
        """
        SELECT * FROM books
        WHERE 
            isInLibrary = 1 AND
            (title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%')
        ORDER BY dateAdded DESC
        """,
    )
    suspend fun getLibraryBooksForQuery(query: String): List<BookEntity>

    @Query("SELECT * FROM books WHERE id=:bookId")
    fun getBookFlow(bookId: Book.Id): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE isbn = :isbn ORDER BY dateAdded DESC")
    suspend fun getBooksByIsbn(isbn: String): List<BookEntity>

    @Query("SELECT COUNT(id) FROM books")
    suspend fun getBookCount(): Int

    @RewriteQueriesToDropUnusedColumns // Removes unused [bookId] columns from the query.
    @Query("SELECT * FROM books WHERE books.isPendingSync = 1")
    fun getAllBooksPendingSyncStream(): Flow<List<BookEntity>>

    @Query("UPDATE books SET isPendingSync = 0 WHERE id = :bookId")
    suspend fun resetBookPendingSyncStatus(bookId: Book.Id)

    /**
     * Checks if the book is part of the user's personal book collection (library).
     */
    @Query("SELECT EXISTS(SELECT 1 FROM books WHERE id = :bookId AND isInLibrary = 1)")
    fun isBookInLibrary(bookId: Book.Id): Flow<Boolean>

    /**
     * Flags the book as part of the user's personal book collection (library).
     */
    @Query(
        """
        UPDATE books 
        SET isInLibrary = 1, dateAdded = :dateAdded, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE id = :bookId
        """,
    )
    suspend fun addToLibraryBooks(bookId: Book.Id, dateAdded: String, lastModifiedTimestamp: String)

    @Insert
    suspend fun insertBook(book: BookEntity)

    @Upsert
    suspend fun insertOrUpdateBook(book: BookEntity)

    @Upsert
    suspend fun insertOrUpdateBooks(bookEntities: List<BookEntity>)

    @Query(
        """
        UPDATE books 
        SET 
            title = :title, 
            author = :author, 
            description = :description,
            subject = :subject, 
            binding = :binding,
            isbn = :isbn, 
            publisher = :publisher, 
            yearPublished = :yearPublished, 
            thumbnailLink = :thumbnailLink,
            userRating = :userRating,
            pageCount = :pageCount, 
            isPendingSync = :isPendingSync,
            lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE id = :bookId
        """,
    )
    suspend fun updateBook(
        bookId: Book.Id,
        isPendingSync: Boolean,
        title: String?,
        author: String?,
        description: String?,
        subject: String?,
        binding: String?,
        isbn: String?,
        publisher: String?,
        yearPublished: Int?,
        thumbnailLink: String?,
        userRating: Int?,
        pageCount: Int?,
        lastModifiedTimestamp: String,
    )

    @Query(
        """
        UPDATE books 
        SET thumbnailLink = :thumbnailLink, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE id = :bookId
        """,
    )
    suspend fun updateBookCover(bookId: Book.Id, thumbnailLink: String, lastModifiedTimestamp: String)

    @Query(
        """
        UPDATE books 
        SET isDeleted = 1, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE id = :bookId
        """,
    )
    suspend fun softDeleteBook(bookId: Book.Id, lastModifiedTimestamp: String)

    @Query("SELECT lastModifiedTimestamp FROM books ORDER BY lastModifiedTimestamp DESC LIMIT 1")
    suspend fun getLatestLastModifiedTimestamp(): String?
}
