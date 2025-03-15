package dev.zezula.books.domain

import dev.zezula.books.data.BookSearchResultsRepository
import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.first
import timber.log.Timber

class FindBookForQueryOnlineUseCase(
    private val onlineBookFinderService: OnlineBookFinderService,
    private val bookSearchResultsRepository: BookSearchResultsRepository,
) {

    suspend operator fun invoke(query: String): Response<List<Book>> {
        return asResponse {
            // Delete old search results from DB
            bookSearchResultsRepository.deleteAllSearchResults()

            // Find new search results
            val searchResults = findBooks(query)

            // Add new search results to DB and to reference search results table
            searchResults.forEach { bookFormData ->
                bookSearchResultsRepository.addBookToSearchResults(bookFormData)
            }

            // Return new search results
            bookSearchResultsRepository.getAllSearchResultsStream().first()
        }
            .onError {
                Timber.e(it, "Failed to search book for query: [$query].")
            }
    }

    private suspend fun findBooks(query: String): List<BookFormData> {
        return onlineBookFinderService.findBookForQueryOnline(query)
    }
}
