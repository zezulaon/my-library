package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import kotlinx.coroutines.flow.Flow

interface UserLibraryRepository {

    fun getAllLibraryBooksStream(): Flow<List<Book>>

    fun getAllBooksForShelfStream(shelfId: String): Flow<List<Book>>

    suspend fun addBookToLibrary(bookFormData: BookFormData): String

    suspend fun moveExistingBookToLibrary(bookId: String)

    fun isBookInLibrary(bookId: String): Flow<Boolean>

    suspend fun updateBookInLibrary(bookId: String, bookFormData: BookFormData)

    suspend fun toggleBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean)

    suspend fun refreshBooks()

    suspend fun searchMyLibraryBooks(query: String): List<Book>
}
