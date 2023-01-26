package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class RefreshLibraryUseCase(private val repository: BooksRepository) {

    suspend operator fun invoke(): Response<Unit> {
        return asResponse {
            repository.refreshBooks()
        }
            .onError {
                Timber.e(it, "Failed to refresh library.")
            }
    }
}
