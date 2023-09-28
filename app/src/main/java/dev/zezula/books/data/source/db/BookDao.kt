package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.reference.ReferenceEntity
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

    // REFERENCES is a reserved keyword in SQLite, so we need to escape it with backticks
    // https://www.sqlite.org/lang_keywords.html
    @Query("SELECT * FROM `references` WHERE bookId = :bookId ORDER BY dateUpdated DESC")
    fun getReferencesForBook(bookId: String): Flow<List<ReferenceEntity>>

    @Upsert
    suspend fun addOrUpdateReference(reference: ReferenceEntity)

    @Query("UPDATE books SET thumbnailLink = :coverUrl WHERE id = :bookId")
    suspend fun updateBookCover(bookId: String, coverUrl: String)

    @Upsert
    suspend fun addOrUpdateNote(note: NoteEntity)
}
