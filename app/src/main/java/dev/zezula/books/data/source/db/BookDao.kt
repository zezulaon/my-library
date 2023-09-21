package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.note.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY dateAdded DESC")
    fun getAllBooksAsStream(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id=:bookId")
    fun getBook(bookId: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE isbn = :isbn ORDER BY dateAdded DESC")
    suspend fun getForIsbn(isbn: String): List<BookEntity>

    @Query("SELECT COUNT(id) FROM books")
    suspend fun getBookCount(): Int

    @Upsert
    suspend fun addOrUpdate(book: BookEntity)

    @Upsert
    suspend fun addOrUpdate(books: List<BookEntity>)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun delete(bookId: String)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)

    @Query("DELETE FROM books")
    suspend fun deleteAll()

    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY dateAdded DESC")
    fun getNotesForBook(bookId: String): Flow<List<NoteEntity>>

    @Upsert
    suspend fun addOrUpdateNote(note: NoteEntity)
}
