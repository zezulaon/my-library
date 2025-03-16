package dev.zezula.books.data

import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.data.model.note.NoteWithBook
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun getAllNotesStream(): Flow<List<NoteWithBook>>

    fun getNotesForBookStream(bookId: String): Flow<List<Note>>

    suspend fun createNote(
        bookId: String,
        noteFormData: NoteFormData,
    )

    suspend fun updateNote(
        noteId: String,
        noteFormData: NoteFormData,
    )

    suspend fun softDeleteNote(noteId: String, bookId: String)
}
