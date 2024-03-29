package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import kotlinx.coroutines.flow.Flow

interface UserLibraryRepository {

    suspend fun deleteBookFromLibrary(bookId: String)

    fun getAllLibraryBooksStream(): Flow<List<Book>>

    suspend fun moveBookToLibrary(bookId: String)

    fun isBookInLibrary(bookId: String): Flow<Boolean>

    fun getBooksForShelfStream(shelfId: String): Flow<List<Book>>

    suspend fun addBook(bookFormData: BookFormData): Book

    suspend fun addOrUpdateBook(bookId: String, bookFormData: BookFormData): Book

    suspend fun updateBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean)

    suspend fun refreshBooks()

    suspend fun searchMyLibraryBooks(query: String): List<Book>
}
