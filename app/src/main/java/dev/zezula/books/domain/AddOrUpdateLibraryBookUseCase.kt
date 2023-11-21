package dev.zezula.books.domain

import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class AddOrUpdateLibraryBookUseCase(private val userLibraryRepository: UserLibraryRepository) {

    /**
     * Updates the book in the app (if [bookId] is available). If [bookId] wasn't provided, then new book record
     * is created in the app.
     */
    suspend operator fun invoke(bookId: String?, bookFormData: BookFormData): Response<Book> {
        return asResponse {
            if (bookId != null) {
                // If there is a bookId, then we update the book.
                userLibraryRepository.addOrUpdateBook(bookId = bookId, bookFormData = bookFormData)
            } else {
                // If there is no bookId, then we create a new book.
                val addedBook = userLibraryRepository.addBook(bookFormData)

                addedBook
            }
        }
            .onError {
                Timber.e(it, "Failed to update the book: [$bookId].")
            }
    }
}
