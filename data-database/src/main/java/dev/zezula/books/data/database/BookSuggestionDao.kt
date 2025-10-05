package dev.zezula.books.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.core.model.Book
import dev.zezula.books.data.database.entities.BookEntity
import dev.zezula.books.data.database.entities.BookSuggestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookSuggestionDao {

    /**
     * Returns suggestions for a given book ID.
     */
    @Query(
        """
        SELECT * FROM books 
        INNER JOIN book_suggestions ON books.id = book_suggestions.bookId 
        WHERE book_suggestions.parentBookId = :bookId
        """,
    )
    fun getAllSuggestionsForBookFlow(bookId: Book.Id): Flow<List<BookEntity>>

    /**
     * Add the book to the "book_suggestions" reference table (Table with book suggestions).
     */
    @Upsert
    suspend fun addToBookSuggestions(bookSuggestionEntity: BookSuggestionEntity)
}
