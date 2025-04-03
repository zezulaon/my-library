package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import kotlinx.coroutines.flow.Flow

interface BookSuggestionsRepository {

    fun getAllSuggestionsForBookFlow(bookId: Book.Id): Flow<List<Book>>

    suspend fun fetchSuggestions(bookId: Book.Id): List<Book>?
}
