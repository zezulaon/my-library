package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.ShelvesRepository
import timber.log.Timber

class DeleteShelfUseCase(private val repository: ShelvesRepository) {

    suspend operator fun invoke(shelf: Shelf): Response<Unit> {
        return asResponse {
            repository.softDeleteShelf(shelf)
        }
            .onError {
                Timber.e(it, "Failed to delete the shelf: [${shelf.id}].")
            }
    }
}
