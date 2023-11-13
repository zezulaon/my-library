package dev.zezula.books.data

import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteFormData
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun getAllNotesStream(): Flow<List<Note>>

    fun getNotesForBookStream(bookId: String): Flow<List<Note>>

    suspend fun addOrUpdateNote(
        noteId: String?,
        bookId: String,
        noteFormData: NoteFormData,
    ): Note

    suspend fun deleteNote(noteId: String, bookId: String)
}
