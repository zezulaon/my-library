package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.ShelvesRepository
import timber.log.Timber

class UpdateShelfUseCase(private val repository: ShelvesRepository) {

    suspend operator fun invoke(shelfId: Shelf.Id, updatedTitle: String): Response<Unit> {
        return asResponse {
            repository.updateShelf(shelfId = shelfId, updatedTitle = updatedTitle)
        }
            .onError {
                Timber.e(it, "Failed to update the shelf: [$shelfId].")
            }
    }
}
