package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Note
import dev.zezula.books.core.model.Rating
import dev.zezula.books.core.model.Review
import dev.zezula.books.core.model.ShelfForBook
import dev.zezula.books.core.utils.combine
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.domain.repositories.BookSuggestionsRepository
import dev.zezula.books.domain.repositories.BooksRepository
import dev.zezula.books.domain.repositories.NotesRepository
import dev.zezula.books.domain.repositories.ReviewsRepository
import dev.zezula.books.domain.repositories.ShelvesRepository
import dev.zezula.books.domain.repositories.UserLibraryRepository
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

    operator fun invoke(bookId: Book.Id): Flow<Response<AllBookDetailResult>> {
        return combine(
            booksRepository.getBookFlow(bookId),
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
