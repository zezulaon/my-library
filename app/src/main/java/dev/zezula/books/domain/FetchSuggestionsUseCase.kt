package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.first
import timber.log.Timber
import kotlin.time.measureTime

class FetchSuggestionsUseCase(
    private val booksRepository: BooksRepository,
) {

    suspend operator fun invoke(bookId: String): Response<List<Book>?> {
        return asResponse {
            var result: List<Book>? = null

            val duration = measureTime {
                val existingSuggestions = booksRepository.getAllSuggestionsForBook(bookId).first()
                // Try to download suggestions if we don't have any
                if (existingSuggestions.isEmpty()) {
                    result = booksRepository.fetchSuggestions(bookId)
                }
            }
            Timber.d("Fetched suggestions for book: [$bookId] in ${duration.inWholeSeconds}s.")

            result
        }
            .onError {
                Timber.e(it, "Failed to download suggestions for book: [$bookId].")
            }
    }
}
