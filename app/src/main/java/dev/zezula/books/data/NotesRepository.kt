package dev.zezula.books.data

import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.data.model.note.NoteWithBook
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun getAllNotesStream(): Flow<List<NoteWithBook>>

    fun getNotesForBookStream(bookId: String): Flow<List<Note>>

    suspend fun addOrUpdateNote(
        noteId: String?,
        bookId: String,
        noteFormData: NoteFormData,
    ): Note

    suspend fun deleteNote(noteId: String, bookId: String)

    fun getAllPendingSyncStream(): Flow<List<NoteEntity>>
    suspend fun resetPendingSyncStatus(noteId: String)
}
