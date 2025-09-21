package dev.zezula.books.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import dev.zezula.books.data.database.entities.BookEntity
import dev.zezula.books.data.database.entities.SearchBookResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchBookResultDao {

    /**
     * Returns search results - books that were found online and are stored temporarily in the database.
     */
    @RewriteQueriesToDropUnusedColumns // Removes unused [bookId] columns from the query.
    @Query(
        """
        SELECT * FROM books 
        INNER JOIN search_book_results ON books.id = search_book_results.bookId
        """,
    )
    fun getAllSearchResultBooksFlow(): Flow<List<BookEntity>>

    /**
     * Add the book to the "search_book_results" reference table (Table with temporary search results).
     */
    @Insert
    suspend fun insertSearchBookResults(searchBookResultEntity: SearchBookResultEntity)

    @Transaction
    suspend fun deleteAllSearchResults() {
        deleteAllSearchedBooksNotInLibrary()
        deleteAllSearchBookResultReferences()
    }

    /**
     * Deletes the books that were found online and are not added in user's library.
     */
    @Query(
        """
        DELETE FROM books
        WHERE books.isInLibrary = 0 AND id IN (SELECT bookId FROM search_book_results)
        """,
    )
    suspend fun deleteAllSearchedBooksNotInLibrary()

    /**
     * Delete all books from the "search_book_results" reference table.
     */
    @Query("DELETE FROM search_book_results")
    suspend fun deleteAllSearchBookResultReferences()
}
