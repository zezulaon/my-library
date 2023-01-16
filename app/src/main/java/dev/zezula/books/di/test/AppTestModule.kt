package dev.zezula.books.di.test

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

val appTestModule = module {
    factory<OnlineBookFinderService> { FakeOnlineBookFinderServiceImpl() }
    factory<NetworkDataSource> { FakeNetworkDataSourceImpl() }
    single<AuthService> { FakeAuthServiceImpl() }

    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(context = androidContext(), klass = AppDatabase::class.java).build()
    }
}