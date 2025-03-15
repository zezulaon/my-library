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

    override suspend fun addOrUpdateNote(
        noteId: String?,
        bookId: String,
        noteFormData: NoteFormData,
    ): Note {
        val finalNoteId = noteId ?: UUID.randomUUID().toString()

        val entity = fromNoteFormData(
            noteId = finalNoteId,
            bookId = bookId,
            noteFormData = noteFormData,
        )
            // FIXME: move setting this flag to DAO
            .copy(isPendingSync = true)

        noteDao.addOrUpdateNote(entity)
        return entity.asExternalModel()
    }

    override suspend fun deleteNote(noteId: String, bookId: String) {
        noteDao.softDeleteNote(noteId)
        noteDao.setPendingSyncStatus(noteId)
    }
}
