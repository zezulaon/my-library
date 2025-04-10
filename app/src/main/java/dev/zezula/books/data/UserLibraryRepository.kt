package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.shelf.Shelf
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
