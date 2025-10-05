package dev.zezula.books.legacy.bookdiary.di

import androidx.room.Room
import dev.zezula.books.legacy.CheckMigrationUseCase
import dev.zezula.books.legacy.bookdiary.BookdiaryCheckMigrationUseCase
import dev.zezula.books.legacy.bookdiary.LegacyAppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val flavoredBookdiaryAppModule = module {

    // Legacy database dependencies
    single<LegacyAppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            LegacyAppDatabase::class.java,
            "books.db",
        )
            .build()
    }
    single {
        val database = get<LegacyAppDatabase>()
        database.legacyBookDao()
    }

    single<CheckMigrationUseCase> { BookdiaryCheckMigrationUseCase(get(), get(), get(), get(), get(), get(), get(), get()) }
}
