package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.UserLibraryRepository
import timber.log.Timber

class AddOrUpdateLibraryBookUseCase(private val userLibraryRepository: UserLibraryRepository) {

    /**
     * Updates the book in the app (if [bookId] is available). If [bookId] wasn't provided, then new book record
     * is created in the app.
     */
    suspend operator fun invoke(bookId: Book.Id?, bookFormData: BookFormData): Response<Unit> {
        return asResponse {
            addOrUpdate(bookId, bookFormData)
        }
            .onError {
                Timber.e(it, "Failed to update the book: [$bookId].")
            }
    }

    private suspend fun addOrUpdate(bookId: Book.Id?, bookFormData: BookFormData) {
        if (bookId != null) {
            // If there is a bookId, then we update the book.
            userLibraryRepository.updateBookInLibrary(bookId = bookId, bookFormData = bookFormData)
        } else {
            // If there is no bookId, then we create a new book.
            userLibraryRepository.addBookToLibrary(bookFormData = bookFormData)
        }
    }
}
