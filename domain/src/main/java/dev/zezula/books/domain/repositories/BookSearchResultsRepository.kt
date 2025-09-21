package dev.zezula.books.domain.repositories

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.BookFormData
import kotlinx.coroutines.flow.Flow

interface BookSearchResultsRepository {

    fun getAllSearchResultsFlow(): Flow<List<Book>>

    suspend fun addBookToSearchResults(bookFormData: BookFormData)

    suspend fun deleteAllSearchResults()
}