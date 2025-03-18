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
            noteDao.addOrUpdateNote(note)
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

    // FIXME: test sync
//    @Test
//    fun note_is_added_and_backed_by_dao_and_network() = runTest {
//        val note = NoteFormData(
//            text = "Text WWW",
//            page = 1,
//            type = "Type X",
//        )
//        val bookId = "888"
//        notesRepository.addOrUpdateNote(
//            noteId = "999",
//            bookId = bookId,
//            noteFormData = note,
//        )
//
//        // Check that note was added to DB
//        assertTrue(
//            noteDao.getNotesForBookStream(bookId)
//                .first()
//                .any { entity ->
//                    note.text == entity.text
//                },
//        )
//
//        // Check that the note was added to network data source
//        assertTrue(
//            networkDataSource.getNotesForBook(bookId)
//                .any { it.text == note.text },
//        )
//
//        // Check that repository returns same IDs as DB
//        assertEquals(
//            noteDao.getAllNotesStream()
//                .first()
//                .map { it.id },
//            notesRepository.getAllNotesStream()
//                .first()
//                .map { it.note.id },
//        )
//    }

//    @Test
    // FIXME: test sync
//    fun data_is_consistent_across_network_and_database_after_update() = runTest {
//        val note = notesTestData.first().copy(text = "Updated Text")
//        val noteFormData = NoteFormData(
//            text = note.text,
//            page = note.page,
//            type = note.type,
//            dateAdded = note.dateAdded,
//        )
//        notesRepository.addOrUpdateNote(note.id, note.bookId, noteFormData)
//
//        val updatedNoteDao = noteDao.getNotesForBookStream(note.bookId).first().first { it.id == note.id }
//        val updatedNoteNetwork = networkDataSource.getNotesForBook(note.bookId).first { it.id == note.id }
//
//        assertEquals(updatedNoteDao.text, updatedNoteNetwork.text)
//    }

    @Test
    fun createNote_without_id_creates_new_note_with_generated_id() = runTest {
        val note = NoteFormData(text = "New Note", page = 10, type = "quote")
        val bookId = "testBookId"
        notesRepository.createNote(bookId, note)

        // FIXME: test
//        assertNotNull(addedNote.id)
//        assertTrue(addedNote.id.isNotBlank())
//        assertEquals(note.text, addedNote.text)
    }

    // FIXME: test sync
//    @Test
//    fun note_is_updated() = runTest {
//        val bookId = notesTestData.first().bookId
//        val noteToUpdate = notesRepository.getNotesForBookStream(bookId).first().first()
//        val updatedText = "new title"
//        notesRepository.addOrUpdateNote(
//            noteId = noteToUpdate.id,
//            bookId = bookId,
//            noteFormData = NoteFormData(text = updatedText),
//        )
//
//        // Check that the book in the repository was updated
//        assertEquals(
//            updatedText,
//            notesRepository.getAllNotesStream()
//                .first().first { it.note.id == noteToUpdate.id }.note.text,
//        )
//        // Check that the book in DB was updated
//        assertEquals(updatedText, noteDao.getAllNotesStream().first().first { it.id == noteToUpdate.id }.text)
//        // Check that the book in network data source was updated
//        assertTrue(
//            networkDataSource.getNotesForBook(bookId)
//                .any { networkNote ->
//                    networkNote.text == updatedText
//                },
//        )
//    }

    // FIXME: test sync
//    @Test
//    fun delete_removes_the_note() = runTest {
//        val noteToDelete = notesRepository.getAllNotesStream().first().first()
//        notesRepository.deleteNote(
//            noteId = noteToDelete.note.id,
//            bookId = noteToDelete.note.bookId,
//        )
//
//        // Check that the book was deleted from all data sources
//        assertNull(notesRepository.getAllNotesStream().first().firstOrNull { it.note.id == noteToDelete.note.id })
//        assertNull(noteDao.getAllNotesStream().first().firstOrNull { it.id == noteToDelete.note.id })
//        assertFalse(
//            networkDataSource.getNotesForBook(noteToDelete.note.bookId)
//                .any { networkNote ->
//                    networkNote.id == noteToDelete.note.id
//                },
//        )
//    }

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
