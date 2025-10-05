package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.domain.repositories.ShelvesRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetShelvesUseCase(private val repository: ShelvesRepository) {

    operator fun invoke(): Flow<Response<List<Shelf>>> {
        return repository.getAllShelvesFlow()
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load shelves")
            }
    }
}
