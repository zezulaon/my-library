package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import kotlinx.coroutines.flow.Flow

interface UserLibraryRepository {

    suspend fun softDeleteBookInLibrary(bookId: String)

    fun getAllLibraryBooksStream(): Flow<List<Book>>

    fun getAllLibraryPendingSyncBooksStream(): Flow<List<Book>>

    fun getAllShelvesWithBooksPendingSyncStream(): Flow<List<ShelfWithBookEntity>>

    fun isBookDeleted(bookId: String): Flow<Boolean>

    suspend fun resetPendingSyncStatus(bookId: String)

    suspend fun resetShelvesWithBooksSyncStatus(shelfId: String, bookId: String)

    suspend fun moveBookToLibrary(bookId: String)

    fun isBookInLibrary(bookId: String): Flow<Boolean>

    fun getBooksForShelfStream(shelfId: String): Flow<List<Book>>

    suspend fun addBook(bookFormData: BookFormData): Book

    suspend fun addOrUpdateBook(bookId: String, bookFormData: BookFormData): Book

    suspend fun updateBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean)

    suspend fun updateBookCover(bookId: String, thumbnailLink: String)

    suspend fun refreshBooks()

    suspend fun searchMyLibraryBooks(query: String): List<Book>
}
