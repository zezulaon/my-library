package dev.zezula.books.data.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.zezula.books.core.utils.di.IO_SCOPE
import dev.zezula.books.data.AuthServiceImpl
import dev.zezula.books.data.BackupService
import dev.zezula.books.data.OnlineBookFinderServiceImpl
import dev.zezula.books.data.database.di.dataDatabaseModule
import dev.zezula.books.data.database.di.testDataDatabaseModuleOverride
import dev.zezula.books.data.fake.FakeAuthServiceImpl
import dev.zezula.books.data.fake.FakeOnlineBookFinderServiceImpl
import dev.zezula.books.data.network.di.dataNetworkModule
import dev.zezula.books.data.network.di.testDataNetworkModuleOverride
import dev.zezula.books.data.repositories.BookSearchResultsRepositoryImpl
import dev.zezula.books.data.repositories.BookSuggestionsRepositoryImpl
import dev.zezula.books.data.repositories.BooksRepositoryImpl
import dev.zezula.books.data.repositories.NotesRepositoryImpl
import dev.zezula.books.data.repositories.ReviewsRepositoryImpl
import dev.zezula.books.data.repositories.ShelvesRepositoryImpl
import dev.zezula.books.data.repositories.UserLibraryRepositoryImpl
import dev.zezula.books.data.repositories.UserRepositoryImpl
import dev.zezula.books.data.sync.SyncUseCase
import dev.zezula.books.domain.repositories.BookSearchResultsRepository
import dev.zezula.books.domain.repositories.BookSuggestionsRepository
import dev.zezula.books.domain.repositories.BooksRepository
import dev.zezula.books.domain.repositories.NotesRepository
import dev.zezula.books.domain.repositories.ReviewsRepository
import dev.zezula.books.domain.repositories.ShelvesRepository
import dev.zezula.books.domain.repositories.UserLibraryRepository
import dev.zezula.books.domain.repositories.UserRepository
import dev.zezula.books.domain.services.AuthService
import dev.zezula.books.domain.services.OnlineBookFinderService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    includes(dataNetworkModule, dataDatabaseModule)

    single<BooksRepository> { BooksRepositoryImpl(get(), get(), get()) }
    single<BookSuggestionsRepository> { BookSuggestionsRepositoryImpl(get(), get(), get()) }
    single<BookSearchResultsRepository> { BookSearchResultsRepositoryImpl(get(), get()) }
    single<UserLibraryRepository> { UserLibraryRepositoryImpl(get(), get()) }
    single<NotesRepository> { NotesRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl() }
    single<ShelvesRepository> { ShelvesRepositoryImpl(get(), get()) }
    single<ReviewsRepository> { ReviewsRepositoryImpl(get(), get(), get(), get()) }

    single<OnlineBookFinderService> { OnlineBookFinderServiceImpl(get(), get(), get()) }
    single<AuthService> { AuthServiceImpl(Firebase.auth) }
    single { SyncUseCase(get(), get(), get(), get(), get(), get()) }

    single<BackupService> {
        BackupService(
            coroutineScope = get(qualifier = named(IO_SCOPE)),
            networkDataSource = get(),
            bookDao = get(),
            shelfDao = get(),
            noteDao = get(),
            shelfAndBookDao = get(),
        )
    }
}

val testDataModuleOverride = module {
    includes(testDataDatabaseModuleOverride, testDataNetworkModuleOverride)

    factory<OnlineBookFinderService> { FakeOnlineBookFinderServiceImpl() }
    single<AuthService> { FakeAuthServiceImpl() }
}
