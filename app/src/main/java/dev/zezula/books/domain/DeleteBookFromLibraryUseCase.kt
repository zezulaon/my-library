package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class DeleteBookFromLibraryUseCase(private val booksRepository: BooksRepository) {

    suspend operator fun invoke(bookId: String): Response<Unit> {
        return asResponse {
            booksRepository.softDeleteBook(bookId)
        }
            .onError {
                Timber.e(it, "Failed to delete the book: [$bookId].")
            }
    }
}
