package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.ReviewsRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.first
import timber.log.Timber

class CheckReviewsDownloadedUseCase(
    private val booksRepository: BooksRepository,
    private val repository: ReviewsRepository,
) {

    suspend operator fun invoke(bookId: Book.Id): Response<Unit> {
        return asResponse {
            val existingReviews = repository.getReviewsForBookFlow(bookId).first()
            // Try to download reviews if we don't have any
            if (existingReviews.isEmpty()) {
                val book = booksRepository.getBook(bookId)
                if (book != null) {
                    repository.refreshReviews(book)
                }
            }
        }
            .onError {
                Timber.e(it, "Failed to download reviews for book: [$bookId].")
            }
    }
}
