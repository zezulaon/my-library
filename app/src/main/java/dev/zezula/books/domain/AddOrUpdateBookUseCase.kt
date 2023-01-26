package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class AddOrUpdateBookUseCase(private val repository: BooksRepository) {

    /**
     * Updates the book in the app (if [bookId] is available). If [bookId] wasn't provided, then new book record
     * is created in the app.
     */
    suspend operator fun invoke(bookId: String?, bookFormData: BookFormData): Response<Book> {
        return asResponse {
            if (bookId != null) {
                repository.addOrUpdateBook(bookId = bookId, bookFormData = bookFormData)
            } else {
                repository.addBook(bookFormData)
            }
        }
            .onError {
                Timber.e(it, "Failed to update the book: [$bookId].")
            }
    }
}
