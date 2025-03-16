package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import kotlinx.coroutines.flow.Flow

interface BookSearchResultsRepository {

    fun getAllSearchResultsStream(): Flow<List<Book>>

    suspend fun addBookToSearchResults(bookFormData: BookFormData)

    suspend fun deleteAllSearchResults()
}
