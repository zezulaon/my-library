package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Review
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.BooksRepository
import dev.zezula.books.domain.repositories.ReviewsRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber

class CheckReviewsDownloadedUseCase(
    private val booksRepository: BooksRepository,
    private val reviewsRepository: ReviewsRepository,
) {

    suspend operator fun invoke(bookId: Book.Id): Response<Unit> {
        return asResponse {
            val existingReviews: List<Review> = reviewsRepository.getReviewsForBookFlow(bookId).first()
            // Try to download reviews if we don't have any
            if (existingReviews.isEmpty()) {
                val book = booksRepository.getBook(bookId)
                if (book != null) {
                    reviewsRepository.refreshReviews(book)
                }
            }
        }
            .onError {
                Timber.e(it, "Failed to download reviews for book: [$bookId].")
            }
    }
}
