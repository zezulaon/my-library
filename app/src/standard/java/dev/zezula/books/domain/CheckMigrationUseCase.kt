package dev.zezula.books.domain

import dev.zezula.books.data.model.MigrationProgress
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class CheckMigrationUseCase() {

    operator fun invoke(migrationProgress: MutableStateFlow<MigrationProgress?>): Response<Unit> {
        return asResponse {
        }
            .onError {
                Timber.e(it, "Failed to to migrate legacy DB.")
            }
    }
}
