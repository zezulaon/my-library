package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.BooksRepository
import timber.log.Timber

class GetBookUseCase(private val repository: BooksRepository) {

    suspend operator fun invoke(bookId: Book.Id): Response<Book?> {
        return asResponse {
            repository.getBook(bookId)
        }
            .onError {
                Timber.e(it, "Failed to get book for id:[$bookId].")
            }
    }
}
