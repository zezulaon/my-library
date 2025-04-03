package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import kotlinx.coroutines.flow.Flow

interface UserLibraryRepository {

    fun getAllLibraryBooksStream(): Flow<List<Book>>

    fun getAllBooksForShelfStream(shelfId: String): Flow<List<Book>>

    suspend fun addBookToLibrary(bookFormData: BookFormData): Book.Id

    suspend fun moveExistingBookToLibrary(bookId: Book.Id)

    fun isBookInLibrary(bookId: Book.Id): Flow<Boolean>

    suspend fun updateBookInLibrary(bookId: Book.Id, bookFormData: BookFormData)

    suspend fun toggleBookInShelf(bookId: Book.Id, shelfId: String, isBookInShelf: Boolean)

    suspend fun searchMyLibraryBooks(query: String): List<Book>
}
