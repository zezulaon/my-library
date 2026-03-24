package dev.zezula.books.di

import androidx.room.Room
import dev.zezula.books.core.fake.FakeAuthServiceImpl
import dev.zezula.books.core.fake.FakeIsbnScannerController
import dev.zezula.books.core.fake.FakeNetworkDataSourceImpl
import dev.zezula.books.core.fake.FakeOnlineBookFinderServiceImpl
import dev.zezula.books.data.database.AppDatabase
import dev.zezula.books.data.network.api.NetworkDataSource
import dev.zezula.books.domain.services.AuthService
import dev.zezula.books.domain.services.OnlineBookFinderService
import dev.zezula.books.scanner.IsbnScannerController
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Provides fake (in memory) DB and fake services, data sources and other controllers. In navigation tests, this module overrides
 * production modules.
 */
val appInstrumentedTestModule = module {

    factory<OnlineBookFinderService> { FakeOnlineBookFinderServiceImpl() }
    single<AuthService> { FakeAuthServiceImpl() }

    single<FakeIsbnScannerController> { FakeIsbnScannerController() }
    single<IsbnScannerController> { get<FakeIsbnScannerController>() }

    factory<NetworkDataSource> { FakeNetworkDataSourceImpl() }

    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(context = androidContext(), klass = AppDatabase::class.java).build()
    }
}
