package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.data.model.note.NoteWithBook
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
