package dev.zezula.books.domain.repositories

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Note
import dev.zezula.books.core.model.NoteFormData
import dev.zezula.books.core.model.NoteWithBook
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun getAllNotesFlow(): Flow<List<NoteWithBook>>

    fun getNotesForBookFlow(bookId: Book.Id): Flow<List<Note>>

    suspend fun createNote(
        bookId: Book.Id,
        noteFormData: NoteFormData,
    )

    suspend fun updateNote(
        noteId: Note.Id,
        noteFormData: NoteFormData,
    )

    suspend fun softDeleteNote(noteId: Note.Id, bookId: Book.Id)
}
