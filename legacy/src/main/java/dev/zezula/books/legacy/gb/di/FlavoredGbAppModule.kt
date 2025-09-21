package dev.zezula.books.legacy.gb.di

import androidx.room.Room
import dev.zezula.books.legacy.CheckMigrationUseCase
import dev.zezula.books.legacy.gb.GbCheckMigrationUseCase
import dev.zezula.books.legacy.gb.LegacyAppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val flavoredGbAppModule = module {

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

    single<CheckMigrationUseCase> { GbCheckMigrationUseCase(get(), get(), get(), get(), get()) }
}
