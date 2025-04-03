package dev.zezula.books.domain

import dev.zezula.books.data.NotesRepository
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class UpdateNoteUseCase(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteId: Note.Id, noteFormData: NoteFormData): Response<Unit> {
        return asResponse {
            notesRepository.updateNote(noteId = noteId, noteFormData = noteFormData)
        }
            .onError {
                Timber.e(it, "Failed to add the shelf.")
            }
    }
}
