package dev.zezula.books.data

import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.data.model.note.NoteWithBook
import dev.zezula.books.data.model.note.NoteWithBookEntity
import dev.zezula.books.data.model.note.asExternalModel
import dev.zezula.books.data.model.note.fromNetworkNote
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

class NotesRepositoryImpl(
    private val noteDao: NoteDao,
    private val networkDataSource: NetworkDataSource,
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
        // Adds or updates a note on the server and then in the database. For newly created notes, a new ID is
        // generated before the note is added to the server.
        val finalNoteId = noteId ?: UUID.randomUUID().toString()
        val networkNote = NetworkNote(
            id = finalNoteId,
            bookId = bookId,
            text = noteFormData.text,
            dateAdded = noteFormData.dateAdded ?: LocalDateTime.now().toString(),
            page = noteFormData.page,
            type = noteFormData.type,
        )
        networkDataSource.addOrUpdateNote(networkNote)

        val networkNoteEntity = fromNetworkNote(
            networkNote = networkNote,
            bookId = bookId,
        )
        noteDao.addOrUpdateNote(networkNoteEntity)
        return networkNoteEntity.asExternalModel()
    }

    override suspend fun deleteNote(noteId: String, bookId: String) {
        // Deletes a note from the server and then from the database.
        networkDataSource.deleteNote(noteId = noteId, bookId = bookId)
        noteDao.deleteNote(noteId)
    }
}
