package dev.zezula.books.di

import dev.zezula.books.data.*
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.fake.FakeBookDaoImpl
import dev.zezula.books.data.source.db.fake.FakeShelfAndBookDaoImpl
import dev.zezula.books.data.source.network.*
import dev.zezula.books.data.source.network.fake.FakeNetworkDataSourceImpl
import dev.zezula.books.data.source.network.fake.FakeOnlineBookFinderServiceImpl
import dev.zezula.books.domain.*
import dev.zezula.books.ui.screen.create.CreateBookViewModel
import dev.zezula.books.ui.screen.detail.BookDetailViewModel
import dev.zezula.books.ui.screen.list.BookListViewModel
import dev.zezula.books.ui.screen.search.SearchBarcodeViewModel
import dev.zezula.books.ui.screen.shelves.ShelvesViewModel
import dev.zezula.books.ui.screen.signin.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appUnitTestModule = module {

    single<OnlineBookFinderService> { FakeOnlineBookFinderServiceImpl() }

    single<BookDao> { FakeBookDaoImpl() }
    single<ShelfAndBookDao> { FakeShelfAndBookDaoImpl() }

    single<NetworkDataSource> { FakeNetworkDataSourceImpl() }

    // UseCases
    single { GetBooksForShelfUseCase(get()) }
    single { RefreshLibraryUseCase(get()) }
    single { GetShelvesUseCase(get()) }
    single { DeleteShelfUseCase(get()) }
    single { UpdateShelfUseCase(get()) }
    single { CreateShelfUseCase(get()) }
    single { GetAllBookDetailUseCase(get(), get(), get()) }
    single { DeleteBookUseCase(get()) }
    single { ToggleBookInShelfUseCase(get()) }
    single { CheckReviewsDownloadedUseCase(get(), get()) }
    single { FindBookOnlineUseCase(get(), get(), get()) }
    single { GetBooksUseCase(get()) }
    single { AddOrUpdateBookUseCase(get()) }

    // Repositories
    single<BooksRepository> { BooksRepositoryImpl(get(), get(), get()) }
    single<ShelvesRepository> { ShelvesRepositoryImpl(get(), get()) }
    single<ReviewsRepository> { ReviewsRepositoryImpl(get(), get(), get()) }

    // ViewModels
    viewModel { BookListViewModel(get(), get(), get()) }
    viewModel { ShelvesViewModel(get(), get(), get(), get()) }
    viewModel { CreateBookViewModel(get(), get(), get()) }
    viewModel { BookDetailViewModel(get(), get(), get(), get(), get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { SearchBarcodeViewModel(get(), get()) }
}