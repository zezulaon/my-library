package dev.zezula.books.data.repositories

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Note
import dev.zezula.books.core.model.NoteFormData
import dev.zezula.books.core.model.NoteWithBook
import dev.zezula.books.data.database.NoteDao
import dev.zezula.books.data.database.entities.NoteEntity
import dev.zezula.books.data.database.entities.NoteWithBookEntity
import dev.zezula.books.data.database.entities.asExternalModel
import dev.zezula.books.data.database.entities.fromNoteFormData
import dev.zezula.books.domain.repositories.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import java.time.LocalDateTime
import java.util.UUID

class NotesRepositoryImpl(
    private val noteDao: NoteDao,
) : NotesRepository {

    override fun getAllNotesFlow(): Flow<List<NoteWithBook>> {
        // Returns a Flow of all notes from the database. Notes are sorted by date added.
        return noteDao.getAllNotesFlow().map {
            it.map(NoteWithBookEntity::asExternalModel)
        }
    }

    override fun getNotesForBookFlow(bookId: Book.Id): Flow<List<Note>> {
        // Returns a Flow of all notes for a given book from the database. Notes are sorted by date added.
        return noteDao.getNotesForBookFlow(bookId).map {
            it.map(NoteEntity::asExternalModel)
        }
    }

    override suspend fun createNote(bookId: Book.Id, noteFormData: NoteFormData) {
        val noteId = Note.Id(UUID.randomUUID().toString())
        val note = fromNoteFormData(
            noteId = noteId,
            bookId = bookId,
            noteFormData = noteFormData,
            dateAdded = noteFormData.dateAdded ?: LocalDateTime.now().toString(),
            lastModifiedTimestamp = Clock.System.now(),
        )
            .copy(isPendingSync = true)
        noteDao.insertNote(note)
    }

    override suspend fun updateNote(noteId: Note.Id, noteFormData: NoteFormData) {
        noteDao.updateNote(
            noteId = noteId,
            text = noteFormData.text,
            type = noteFormData.type,
            page = noteFormData.page,
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
    }

    override suspend fun softDeleteNote(noteId: Note.Id, bookId: Book.Id) {
        noteDao.softDeleteNote(
            noteId = noteId,
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
    }
}