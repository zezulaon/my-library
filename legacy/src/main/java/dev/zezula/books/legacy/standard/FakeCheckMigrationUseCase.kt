package dev.zezula.books.legacy.standard

import dev.zezula.books.core.model.MigrationProgress
import dev.zezula.books.domain.model.Response
import dev.zezula.books.legacy.CheckMigrationUseCase
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCheckMigrationUseCase : CheckMigrationUseCase {

    override suspend operator fun invoke(migrationProgress: MutableStateFlow<MigrationProgress?>): Response<Unit> {
        return Response.Success(Unit)
    }
}
