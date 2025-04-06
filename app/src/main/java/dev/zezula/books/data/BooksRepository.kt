package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun getBookFlow(bookId: Book.Id): Flow<Book?>

    suspend fun getBook(bookId: Book.Id): Book?

    suspend fun getBooksByIsbn(isbn: String): List<Book>

    suspend fun updateBookCover(bookId: Book.Id, thumbnailLink: String)

    suspend fun softDeleteBook(bookId: Book.Id)
}
