package dev.zezula.books.domain

import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
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
