package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class DeleteBookUseCase(private val repository: BooksRepository) {

    suspend operator fun invoke(bookId: String): Response<Unit> {
        return asResponse {
            repository.deleteBook(bookId)
        }
            .onError {
                Timber.e(it, "Failed to delete the book: [$bookId].")
            }
    }
}