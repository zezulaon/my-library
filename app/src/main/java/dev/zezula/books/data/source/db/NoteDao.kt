package dev.zezula.books.data.source.db

import androidx.room.Dao
import androidx.room.Insert
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
    fun getAllNotesFlow(): Flow<List<NoteWithBookEntity>>

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
    fun getNotesForBookFlow(bookId: String): Flow<List<NoteEntity>>

    @Insert
    suspend fun insertNote(noteEntity: NoteEntity)

    @Upsert
    suspend fun insertOrUpdateNote(note: NoteEntity)

    @Upsert
    suspend fun insertOrUpdateNotes(noteEntities: List<NoteEntity>)

    @Query(
        """
        UPDATE notes 
        SET text = :text, page = :page, type = :type, lastModifiedTimestamp = :lastModifiedTimestamp, isPendingSync = 1
        WHERE id = :noteId
        """,
    )
    suspend fun updateNote(
        noteId: String,
        text: String,
        page: Int?,
        type: String?,
        lastModifiedTimestamp: String,
    )

    @Query(
        """
        UPDATE notes 
        SET isDeleted = 1, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE id = :noteId
        """,
    )
    suspend fun softDeleteNote(noteId: String, lastModifiedTimestamp: String)

    @Query(
        """
        UPDATE notes 
        SET isDeleted = 1, isPendingSync = 1, lastModifiedTimestamp = :lastModifiedTimestamp
        WHERE bookId = :bookId
        """,
    )
    suspend fun softDeleteNotesForBook(bookId: String, lastModifiedTimestamp: String)

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

    @Query("SELECT lastModifiedTimestamp FROM notes ORDER BY lastModifiedTimestamp DESC LIMIT 1")
    suspend fun getLatestLastModifiedTimestamp(): String?
}
