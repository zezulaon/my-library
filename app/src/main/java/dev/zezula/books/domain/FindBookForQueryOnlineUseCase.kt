package dev.zezula.books.domain

import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class FindBookForQueryOnlineUseCase(
    private val onlineBookFinderService: OnlineBookFinderService,
) {

    suspend operator fun invoke(query: String): Response<List<BookFormData>> {
        return asResponse {
            findBooks(query)
        }
            .onError {
                Timber.e(it, "Failed to search book for query: [$query].")
            }
    }

    private suspend fun findBooks(query: String): List<BookFormData> {
        return onlineBookFinderService.findBookForQueryOnline(query)
    }
}
