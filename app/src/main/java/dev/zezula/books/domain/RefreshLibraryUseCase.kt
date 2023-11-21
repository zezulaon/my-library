package dev.zezula.books.domain

import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class RefreshLibraryUseCase(private val userLibraryRepository: UserLibraryRepository) {

    suspend operator fun invoke(): Response<Unit> {
        return asResponse {
            userLibraryRepository.refreshBooks()
        }
            .onError {
                Timber.e(it, "Failed to refresh library.")
            }
    }
}
