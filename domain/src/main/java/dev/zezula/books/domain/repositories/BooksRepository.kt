package dev.zezula.books.domain.repositories

import dev.zezula.books.core.model.Book
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun getBookFlow(bookId: Book.Id): Flow<Book?>

    suspend fun getBook(bookId: Book.Id): Book?

    suspend fun getBooksByIsbn(isbn: String): List<Book>

    suspend fun updateBookCover(bookId: Book.Id, thumbnailLink: String)

    suspend fun softDeleteBook(bookId: Book.Id)
}
