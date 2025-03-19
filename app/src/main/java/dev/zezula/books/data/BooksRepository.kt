package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun getBookFlow(bookId: String): Flow<Book?>

    suspend fun getBook(bookId: String): Book?

    suspend fun getBooksByIsbn(isbn: String): List<Book>

    suspend fun updateBookCover(bookId: String, thumbnailLink: String)

    suspend fun softDeleteBook(bookId: String)
}
