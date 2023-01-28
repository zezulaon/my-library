package dev.zezula.books.di

import androidx.room.Room
import dev.zezula.books.data.source.db.AppDatabase
import dev.zezula.books.data.source.network.AuthService
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.data.source.network.fake.FakeAuthServiceImpl
import dev.zezula.books.data.source.network.fake.FakeNetworkDataSourceImpl
import dev.zezula.books.data.source.network.fake.FakeOnlineBookFinderServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Provides fake (in memory) DB and fake services and data sources. In navigation tests, this module overrides
 * production modules.
 */
val appInstrumentedTestModule = module {
    factory<OnlineBookFinderService> { FakeOnlineBookFinderServiceImpl() }
    factory<NetworkDataSource> { FakeNetworkDataSourceImpl() }
    single<AuthService> { FakeAuthServiceImpl() }

    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(context = androidContext(), klass = AppDatabase::class.java).build()
    }
}
