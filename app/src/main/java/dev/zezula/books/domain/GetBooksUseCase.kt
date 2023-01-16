package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class GetBooksUseCase(private val repository: BooksRepository) {

    suspend operator fun invoke(bookId: String): Response<Book?> {

        return asResponse {
            repository.getBook(bookId)
        }
            .onError {
                Timber.e(it, "Failed to get book for id:[$bookId].")
            }
    }
}