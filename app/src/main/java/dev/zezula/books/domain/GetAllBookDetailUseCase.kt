package dev.zezula.books.domain

import dev.zezula.books.data.BookSuggestionsRepository
import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.NotesRepository
import dev.zezula.books.data.ReviewsRepository
import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.review.Rating
import dev.zezula.books.data.model.review.Review
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.util.combine
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

data class AllBookDetailResult(
    val book: Book? = null,
    val notes: List<Note> = emptyList(),
    val rating: Rating? = null,
    val shelves: List<ShelfForBook> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val suggestions: List<Book> = emptyList(),
    val isBookInLibrary: Boolean = false,
)

class GetAllBookDetailUseCase(
    private val shelvesRepository: ShelvesRepository,
    private val booksRepository: BooksRepository,
    private val bookSuggestionsRepository: BookSuggestionsRepository,
    private val userLibraryRepository: UserLibraryRepository,
    private val reviewsRepository: ReviewsRepository,
    private val notesRepository: NotesRepository,
) {

    operator fun invoke(bookId: String): Flow<Response<AllBookDetailResult>> {
        return combine(
            booksRepository.getBookStream(bookId),
            userLibraryRepository.isBookInLibrary(bookId),
            notesRepository.getNotesForBookFlow(bookId),
            shelvesRepository.getAllShelvesForBookFlow(bookId),
            reviewsRepository.getRatingForBookFlow(bookId),
            reviewsRepository.getReviewsForBookFlow(bookId),
            bookSuggestionsRepository.getAllSuggestionsForBookFlow(bookId),
        ) { book, isBookInLibrary, notes, shelves, rating, reviews, suggestions ->
            AllBookDetailResult(
                book = book,
                isBookInLibrary = isBookInLibrary,
                notes = notes,
                rating = rating,
                shelves = shelves,
                reviews = reviews,
                suggestions = suggestions,
            )
        }
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load book detail")
            }
    }
}
