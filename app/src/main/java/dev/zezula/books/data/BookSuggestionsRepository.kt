package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import kotlinx.coroutines.flow.Flow

interface BookSuggestionsRepository {

    suspend fun fetchSuggestions(bookId: String): List<Book>?

    fun getAllSuggestionsForBook(bookId: String): Flow<List<Book>>
}
