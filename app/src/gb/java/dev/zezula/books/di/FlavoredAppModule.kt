package dev.zezula.books.di

import androidx.room.Room
import dev.zezula.books.data.source.db.legacy.LegacyAppDatabase
import dev.zezula.books.domain.CheckMigrationUseCase
import dev.zezula.books.domain.RefreshBookCoverUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val flavoredAppModule = module {

    // Legacy database dependencies
    single<LegacyAppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            LegacyAppDatabase::class.java,
            "gb_googlebooks.db",
        )
            .build()
    }
    single {
        val database = get<LegacyAppDatabase>()
        database.legacyBookDao()
    }

    single { RefreshBookCoverUseCase(get(), get(), get()) }
    single { CheckMigrationUseCase(get(), get(), get(), get(), get(), get(), get(), get()) }
}
