package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Note
import dev.zezula.books.core.model.NoteFormData
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.NotesRepository
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
