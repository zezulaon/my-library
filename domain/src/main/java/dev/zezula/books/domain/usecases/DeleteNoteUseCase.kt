package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Note
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.NotesRepository
import timber.log.Timber

class DeleteNoteUseCase(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteId: Note.Id, bookId: Book.Id): Response<Unit> {
        return asResponse {
            notesRepository.softDeleteNote(noteId = noteId, bookId = bookId)
        }
            .onError {
                Timber.e(it, "Failed to delete the book: [$bookId].")
            }
    }
}