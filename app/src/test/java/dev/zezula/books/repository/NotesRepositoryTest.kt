package dev.zezula.books.repository

import dev.zezula.books.data.NotesRepository
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.data.model.note.asExternalModel
import dev.zezula.books.data.model.note.fromNoteFormData
import dev.zezula.books.data.model.note.previewNoteEntities
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.di.appUnitTestModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import java.time.LocalDateTime
import kotlin.test.assertEquals

class NotesRepositoryTest : KoinTest {

    private val notesRepository: NotesRepository by inject()
    private val noteDao: NoteDao by inject()
    private val networkDataSource: NetworkDataSource by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appUnitTestModule)
    }

    private val notesTestData = listOf(previewNoteEntities.first())

    @Before
    fun setupRepository() = runTest {
        notesTestData.forEach { note ->
            noteDao.insertOrUpdateNote(note)
        }
    }

    @Test
    fun notes_stream_is_backed_by_note_dao() = runTest {
        assertEquals(
            noteDao.getNotesForBookFlow(notesTestData.first().bookId)
                .first()
                .map(NoteEntity::asExternalModel),
            notesRepository.getNotesForBookFlow(notesTestData.first().bookId)
                .first(),
        )
    }

    @Test
    fun getAllNotesStream_returns_all_notes() = runTest {
        val allNotes = notesRepository.getAllNotesFlow().first()
        // Check that returned note IDs match the test data
        assertEquals(notesTestData.map { it.id }, allNotes.map { it.note.id })
    }

    @Test
    fun createNote_without_id_creates_new_note_with_generated_id() = runTest {
        val note = NoteFormData(text = "New Note", page = 10, type = "quote")
        val bookId = "testBookId"
        notesRepository.createNote(bookId, note)
    }


    @Test
    fun data_transformation_is_correct() = runTest {
        val networkNote = NetworkNote(
            id = "newId",
            bookId = "newBookId",
            text = "New Network Note",
            dateAdded = LocalDateTime.now().toString(),
            page = 100,
            type = "Inspiration",
        )
        val transformedNoteEntity = fromNoteFormData(networkNote = networkNote, bookId = networkNote.bookId!!)
        val transformedNote = transformedNoteEntity.asExternalModel()

        assertEquals(networkNote.id, transformedNote.id)
        assertEquals(networkNote.bookId, transformedNote.bookId)
        assertEquals(networkNote.text, transformedNote.text)
        assertEquals(networkNote.dateAdded, transformedNote.dateAdded)
        assertEquals(networkNote.page, transformedNote.page)
        assertEquals(networkNote.type, transformedNote.type)
    }

    @Test
    fun addOrUpdateNote_is_idempotent() = runTest {
        val note = NoteFormData(text = "Idempotent Note", page = 5, type = "General")
        val bookId = "idempotentBook"
        val noteId = "idempotentNoteId"

        notesRepository.addOrUpdateNote(noteId, bookId, note)
        notesRepository.addOrUpdateNote(noteId, bookId, note) // Repeat the operation

        val allNotesWithId = noteDao.getNotesForBookFlow(bookId).first().count { it.id == noteId }

        assertEquals(1, allNotesWithId)
    }
}
