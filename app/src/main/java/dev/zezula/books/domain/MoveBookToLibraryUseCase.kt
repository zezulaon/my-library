package dev.zezula.books.domain

import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class MoveBookToLibraryUseCase(private val userLibraryRepository: UserLibraryRepository) {

    suspend operator fun invoke(bookId: String): Response<Unit> {
        return asResponse {
            userLibraryRepository.moveBookToLibrary(bookId)
        }
            .onError {
                Timber.e(it, "Failed to add the book: [$bookId] to library.")
            }
    }
}
