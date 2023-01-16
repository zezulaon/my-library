package dev.zezula.books.domain

import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class UpdateShelfUseCase(private val repository: ShelvesRepository) {

    suspend operator fun invoke(shelfId: String, updatedTitle: String): Response<Unit> {
        return asResponse {
            repository.updateShelf(shelfId = shelfId, updatedTitle = updatedTitle)
        }
            .onError {
                Timber.e(it, "Failed to update the shelf: [$shelfId].")
            }
    }
}