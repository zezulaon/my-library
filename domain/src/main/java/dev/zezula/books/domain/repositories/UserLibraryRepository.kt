package dev.zezula.books.domain.repositories

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.core.model.Shelf
import kotlinx.coroutines.flow.Flow

interface UserLibraryRepository {

    fun getAllLibraryBooksFlow(): Flow<List<Book>>

    fun getAllBooksForShelfStream(shelfId: Shelf.Id): Flow<List<Book>>

    suspend fun addBookToLibrary(bookFormData: BookFormData): Book.Id

    suspend fun moveExistingBookToLibrary(bookId: Book.Id)

    fun isBookInLibrary(bookId: Book.Id): Flow<Boolean>

    suspend fun updateBookInLibrary(bookId: Book.Id, bookFormData: BookFormData)

    suspend fun toggleBookInShelf(bookId: Book.Id, shelfId: Shelf.Id, isBookInShelf: Boolean)

    suspend fun searchMyLibraryBooks(query: String): List<Book>
}