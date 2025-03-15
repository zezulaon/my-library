package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import kotlinx.coroutines.flow.Flow

interface BookSearchResultsRepository {
    fun getAllSearchResultsStream(): Flow<List<Book>>
    suspend fun addBookToSearchResults(bookId: String)
    suspend fun deleteAllSearchResults()
}
