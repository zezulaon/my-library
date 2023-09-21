package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteFormData
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun getBooksForShelfAsStream(shelfId: String): Flow<List<Book>>

    fun getAllBooksStream(): Flow<List<Book>>

    fun getBookStream(bookId: String): Flow<Book?>

    fun getNotesStream(bookId: String): Flow<List<Note>>

    suspend fun addOrUpdateNote(
        noteId: String?,
        bookId: String,
        noteFormData: NoteFormData,
    ): Note

    suspend fun getBook(bookId: String): Book?

    suspend fun getBookId(isbn: String): String?

    suspend fun addBook(bookFormData: BookFormData): Book

    suspend fun addOrUpdateBook(bookId: String, bookFormData: BookFormData): Book

    suspend fun deleteBook(bookId: String)

    suspend fun deleteNote(noteId: String, bookId: String)

    suspend fun updateBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean)

    suspend fun refreshBooks()
}
