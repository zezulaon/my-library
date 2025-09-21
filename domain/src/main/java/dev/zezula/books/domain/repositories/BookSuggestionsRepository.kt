package dev.zezula.books.domain.repositories

import dev.zezula.books.core.model.Book
import kotlinx.coroutines.flow.Flow

interface BookSuggestionsRepository {

    fun getAllSuggestionsForBookFlow(bookId: Book.Id): Flow<List<Book>>

    suspend fun fetchSuggestions(bookId: Book.Id): List<Book>?
}