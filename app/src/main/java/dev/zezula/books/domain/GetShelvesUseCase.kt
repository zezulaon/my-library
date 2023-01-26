package dev.zezula.books.domain

import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetShelvesUseCase(private val repository: ShelvesRepository) {

    operator fun invoke(): Flow<Response<List<Shelf>>> {
        return repository.getShelvesAsStream()
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load shelves")
            }
    }
}
