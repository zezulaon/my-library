package dev.zezula.books.legacy

import dev.zezula.books.core.model.MigrationProgress
import dev.zezula.books.domain.model.Response
import kotlinx.coroutines.flow.MutableStateFlow

interface CheckMigrationUseCase {
    suspend operator fun invoke(migrationProgress: MutableStateFlow<MigrationProgress?>): Response<Unit>
}
