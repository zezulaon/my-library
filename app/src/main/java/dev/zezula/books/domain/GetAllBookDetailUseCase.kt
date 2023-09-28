package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.ReviewsRepository
import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.reference.Reference
import dev.zezula.books.data.model.review.Rating
import dev.zezula.books.data.model.review.Review
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.util.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import timber.log.Timber

data class AllBookDetailResult(
    val book: Book? = null,
    val notes: List<Note> = emptyList(),
    val references: List<Reference> = emptyList(),
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
            booksRepository.getNotesStream(bookId),
            booksRepository.getReferencesStream(bookId),
            shelvesRepository.getShelvesForBookAsStream(bookId),
            reviewsRepository.getRatingStream(bookId),
            reviewsRepository.getReviewsForBookAsStream(bookId),
        ) { book, notes, references, shelves, rating, reviews ->
            AllBookDetailResult(
                book = book,
                notes = notes,
                // For now, we only want to display Google references
                references = references.filter { it.id.contains(other = "google", ignoreCase = true) },
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
