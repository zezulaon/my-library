package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun getAllBooksStream(): Flow<List<Book>>

    fun getBookStream(bookId: String): Flow<Book?>

    suspend fun addBook(bookFormData: BookFormData): Book

    suspend fun updateBook(bookId: String, bookFormData: BookFormData): Book

    suspend fun getBook(bookId: String): Book?

    suspend fun getBookId(isbn: String): String?

    suspend fun deleteBook(bookId: String)
}
