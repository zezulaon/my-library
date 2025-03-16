package dev.zezula.books.data

import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.data.model.note.NoteWithBook
import dev.zezula.books.data.model.note.NoteWithBookEntity
import dev.zezula.books.data.model.note.asExternalModel
import dev.zezula.books.data.model.note.fromNoteFormData
import dev.zezula.books.data.source.db.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class NotesRepositoryImpl(
    private val noteDao: NoteDao,
) : NotesRepository {

    override fun getAllNotesStream(): Flow<List<NoteWithBook>> {
        // Returns a Flow of all notes from the database. Notes are sorted by date added.
        return noteDao.getAllNotesStream().map {
            it.map(NoteWithBookEntity::asExternalModel)
        }
    }

    override fun getNotesForBookStream(bookId: String): Flow<List<Note>> {
        // Returns a Flow of all notes for a given book from the database. Notes are sorted by date added.
        return noteDao.getNotesForBookStream(bookId).map {
            it.map(NoteEntity::asExternalModel)
        }
    }

    override suspend fun createNote(bookId: String, noteFormData: NoteFormData) {
        val noteId = UUID.randomUUID().toString()
        val note = fromNoteFormData(
            noteId = noteId,
            bookId = bookId,
            noteFormData = noteFormData,
        )
            .copy(isPendingSync = true)
        noteDao.insertNote(note)
    }

    override suspend fun updateNote(noteId: String, noteFormData: NoteFormData) {
        noteDao.updateNote(
            noteId = noteId,
            text = noteFormData.text,
            type = noteFormData.type,
            page = noteFormData.page,
        )
    }

    override suspend fun softDeleteNote(noteId: String, bookId: String) {
        noteDao.softDeleteNote(noteId)
    }
}
