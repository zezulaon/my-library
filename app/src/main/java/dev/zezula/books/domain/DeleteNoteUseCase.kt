package dev.zezula.books.domain

import dev.zezula.books.data.NotesRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class DeleteNoteUseCase(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(noteId: String, bookId: Book.Id): Response<Unit> {
        return asResponse {
            notesRepository.softDeleteNote(noteId = noteId, bookId = bookId)
        }
            .onError {
                Timber.e(it, "Failed to delete the book: [$bookId].")
            }
    }
}
