package dev.zezula.books.domain

import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class CreateShelfUseCase(private val repository: ShelvesRepository) {

    suspend operator fun invoke(shelfTitle: String): Response<Unit> {
        return asResponse {
            repository.createShelf(shelfTitle)
        }
            .onError {
                Timber.e(it, "Failed to add the shelf.")
            }
    }
}