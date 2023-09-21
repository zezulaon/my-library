package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class DeleteNoteUseCase(private val repository: BooksRepository) {

    suspend operator fun invoke(noteId: String, bookId: String): Response<Unit> {
        return asResponse {
            repository.deleteNote(noteId = noteId, bookId = bookId)
        }
            .onError {
                Timber.e(it, "Failed to delete the book: [$bookId].")
            }
    }
}
