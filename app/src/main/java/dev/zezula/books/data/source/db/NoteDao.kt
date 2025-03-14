package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.note.NoteWithBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    /**
     * Returns a flow of all notes that are associated with a book in the user's library.
     */
    @Query(
        """
            SELECT notes.*, books.title as bookTitle 
            FROM notes 
            INNER JOIN books ON notes.bookId = books.id 
            WHERE notes.isDeleted = 0
            ORDER BY dateAdded DESC
            """,
    )
    fun getAllNotesStream(): Flow<List<NoteWithBookEntity>>

    /**
     * Returns a flow of notes associated with a specific [bookId], ordered by their addition date.
     */
    @Query(
        """
        SELECT * FROM notes 
        WHERE bookId = :bookId AND isDeleted = 0 
        ORDER BY dateAdded DESC
        """,
    )
    fun getNotesForBookStream(bookId: String): Flow<List<NoteEntity>>

    /**
     * Adds a new note or updates an existing one in the database based on the provided [note].
     * Utilizes upsert logic: updates the note if it exists, or inserts a new one if it doesn't.
     */
    @Upsert
    suspend fun addOrUpdateNote(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1 WHERE id = :noteId")
    suspend fun softDeleteNote(noteId: String)

    @Query(
        """
        UPDATE notes 
        SET isDeleted = 1, isPendingSync = 1
        WHERE bookId = :bookId
        """,
    )
    suspend fun softDeleteNotesForBook(bookId: String)

    @Query(
        """
        UPDATE notes 
        SET isPendingSync = 1
        WHERE id = :noteId
        """,
    )
    suspend fun setPendingSyncStatus(noteId: String)

    @Query(
        """
        UPDATE notes 
        SET isPendingSync = 0
        WHERE id = :noteId
        """,
    )
    suspend fun resetPendingSyncStatus(noteId: String)

    @Query(
        """
        SELECT * FROM notes 
        WHERE isPendingSync = 1
        """,
    )
    fun getAllPendingSyncStream(): Flow<List<NoteEntity>>
}
