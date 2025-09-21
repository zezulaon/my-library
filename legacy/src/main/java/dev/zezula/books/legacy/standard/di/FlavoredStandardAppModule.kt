package dev.zezula.books.legacy.standard.di

import dev.zezula.books.legacy.CheckMigrationUseCase
import dev.zezula.books.legacy.standard.FakeCheckMigrationUseCase
import org.koin.dsl.module

val flavoredStandardAppModule = module {
    single<CheckMigrationUseCase> { FakeCheckMigrationUseCase() }
}
