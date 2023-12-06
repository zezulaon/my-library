package dev.zezula.books.di

import dev.zezula.books.domain.CheckMigrationUseCase
import dev.zezula.books.domain.RefreshBookCoverUseCase
import org.koin.dsl.module

val flavoredAppModule = module {
    single { CheckMigrationUseCase() }
    single { RefreshBookCoverUseCase(get(), get(), get()) }
}
