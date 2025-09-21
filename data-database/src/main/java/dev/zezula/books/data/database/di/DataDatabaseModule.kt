package dev.zezula.books.data.database.di

import androidx.room.Room
import dev.zezula.books.data.database.AppDatabase
import dev.zezula.books.data.database.MIGRATION_3_4
import dev.zezula.books.data.database.MIGRATION_6_7
import dev.zezula.books.data.database.MIGRATION_9_10
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataDatabaseModule = module {

    // Database and DAOs
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "app_database",
        )
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_6_7)
            .addMigrations(MIGRATION_9_10)
            .build()
    }
    single {
        val database = get<AppDatabase>()
        database.bookDao()
    }
    single {
        val database = get<AppDatabase>()
        database.bookSearchResultDao()
    }
    single {
        val database = get<AppDatabase>()
        database.bookSuggestionDao()
    }
    single {
        val database = get<AppDatabase>()
        database.noteDao()
    }
    single {
        val database = get<AppDatabase>()
        database.reviewDao()
    }
    single {
        val database = get<AppDatabase>()
        database.ratingDao()
    }
    single {
        val database = get<AppDatabase>()
        database.shelfDao()
    }
    single {
        val database = get<AppDatabase>()
        database.shelfAndBookDao()
    }
}

val testDataDatabaseModuleOverride = module {
    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(context = androidContext(), klass = AppDatabase::class.java).build()
    }
}