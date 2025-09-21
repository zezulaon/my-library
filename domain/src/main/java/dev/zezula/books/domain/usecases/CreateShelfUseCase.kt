package dev.zezula.books.domain.usecases

import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.ShelvesRepository
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