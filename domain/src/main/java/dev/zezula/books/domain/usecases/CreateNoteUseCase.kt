package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.NoteFormData
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.NotesRepository
import timber.log.Timber

class CreateNoteUseCase(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(bookId: Book.Id, noteFormData: NoteFormData): Response<Unit> {
        return asResponse {
            notesRepository.createNote(bookId = bookId, noteFormData = noteFormData)
        }
            .onError {
                Timber.e(it, "Failed to add the shelf.")
            }
    }
}