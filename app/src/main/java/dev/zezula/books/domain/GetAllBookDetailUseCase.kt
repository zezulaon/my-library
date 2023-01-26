package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.ReviewsRepository
import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.review.Rating
import dev.zezula.books.data.model.review.Review
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import timber.log.Timber

data class AllBookDetailResult(
    val book: Book? = null,
    val rating: Rating? = null,
    val shelves: List<ShelfForBook> = emptyList(),
    val reviews: List<Review> = emptyList(),
)

class GetAllBookDetailUseCase(
    private val shelvesRepository: ShelvesRepository,
    private val booksRepository: BooksRepository,
    private val reviewsRepository: ReviewsRepository,
) {

    operator fun invoke(bookId: String): Flow<Response<AllBookDetailResult>> {
        return combine(
            booksRepository.getBookStream(bookId),
            shelvesRepository.getShelvesForBookAsStream(bookId),
            reviewsRepository.getRatingStream(bookId),
            reviewsRepository.getReviewsForBookAsStream(bookId),
        ) { book, shelves, rating, reviews ->
            AllBookDetailResult(
                book = book,
                rating = rating,
                shelves = shelves,
                reviews = reviews,
            )
        }
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load book detail")
            }
    }
}
