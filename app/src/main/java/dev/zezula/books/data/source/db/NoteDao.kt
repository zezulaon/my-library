package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.zezula.books.data.model.note.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    /**
     * Returns a flow of all notes in the database, ordered by their addition date.
     */
    @Query("SELECT * FROM notes ORDER BY dateAdded DESC")
    fun getAllNotesStream(): Flow<List<NoteEntity>>

    /**
     * Returns a flow of notes associated with a specific [bookId], ordered by their addition date.
     */
    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY dateAdded DESC")
    fun getNotesForBookStream(bookId: String): Flow<List<NoteEntity>>

    /**
     * Adds a new note or updates an existing one in the database based on the provided [note].
     * Utilizes upsert logic: updates the note if it exists, or inserts a new one if it doesn't.
     */
    @Upsert
    suspend fun addOrUpdateNote(note: NoteEntity)

    /**
     * Deletes the note identified by [noteId] from the database.
     */
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)
}
