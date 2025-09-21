package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.UserLibraryRepository
import timber.log.Timber

class SearchMyLibraryBooksUseCase(private val userLibraryRepository: UserLibraryRepository) {

    suspend operator fun invoke(query: String): Response<List<Book>> {
        return asResponse {
            userLibraryRepository.searchMyLibraryBooks(query)
        }
            .onError {
                Timber.e(it, "Failed to search book for query: [$query].")
            }
    }
}