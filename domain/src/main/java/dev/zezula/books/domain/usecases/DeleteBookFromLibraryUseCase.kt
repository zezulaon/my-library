package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.BooksRepository
import timber.log.Timber

class DeleteBookFromLibraryUseCase(private val booksRepository: BooksRepository) {

    suspend operator fun invoke(bookId: Book.Id): Response<Unit> {
        return asResponse {
            booksRepository.softDeleteBook(bookId)
        }
            .onError {
                Timber.e(it, "Failed to delete the book: [$bookId].")
            }
    }
}