package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.note.NoteWithBookEntity
import dev.zezula.books.data.source.db.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNoteDaoImpl : NoteDao {

    // Internal storage for notes, mimicking a database table
    private val notes = mutableListOf<NoteEntity>()

    // MutableStateFlow for emitting updates to the notes list
    private val notesFlow = MutableStateFlow<List<NoteEntity>>(listOf())

    override fun getAllNotesStream(): Flow<List<NoteWithBookEntity>> {
        return notesFlow.map { noteList ->
            noteList.map { note ->
                NoteWithBookEntity(
                    id = note.id,
                    bookId = note.bookId,
                    text = note.text,
                    dateAdded = note.dateAdded,
                    page = note.page,
                    type = note.type,
                    bookTitle = "Book Title",
                )
            }
                .sortedByDescending { it.dateAdded }
        }
    }

    override suspend fun softDeleteNotesForBook(bookId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun resetPendingSyncStatus(noteId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun softDeleteNote(noteId: String) {
        TODO("Not yet implemented")
    }

    override fun getAllPendingSyncStream(): Flow<List<NoteEntity>> {
        TODO("Not yet implemented")
    }

    override fun getNotesForBookStream(bookId: String): Flow<List<NoteEntity>> {
        return notesFlow
            .map { noteList ->
                noteList
                    .filter { it.bookId == bookId }
                    .sortedByDescending { it.dateAdded }
            }
    }

    override suspend fun insertNote(noteEntity: NoteEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateNote(noteId: String, text: String, page: Int?, type: String?) {
        TODO("Not yet implemented")
    }
}
