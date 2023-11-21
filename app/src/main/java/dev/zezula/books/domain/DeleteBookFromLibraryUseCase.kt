package dev.zezula.books.domain

import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class DeleteBookFromLibraryUseCase(private val userLibraryRepository: UserLibraryRepository) {

    suspend operator fun invoke(bookId: String): Response<Unit> {
        return asResponse {
            userLibraryRepository.deleteBookFromLibrary(bookId)
        }
            .onError {
                Timber.e(it, "Failed to delete the book: [$bookId].")
            }
    }
}
