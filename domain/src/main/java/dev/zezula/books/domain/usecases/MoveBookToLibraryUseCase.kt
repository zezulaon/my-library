package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.UserLibraryRepository
import timber.log.Timber

class MoveBookToLibraryUseCase(private val userLibraryRepository: UserLibraryRepository) {

    suspend operator fun invoke(bookId: Book.Id): Response<Unit> {
        return asResponse {
            userLibraryRepository.moveExistingBookToLibrary(bookId)
        }
            .onError {
                Timber.e(it, "Failed to add the book: [$bookId] to library.")
            }
    }
}