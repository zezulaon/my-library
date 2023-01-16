package dev.zezula.books.di

import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.zezula.books.data.*
import dev.zezula.books.data.source.db.AppDatabase
import dev.zezula.books.data.source.network.*
import dev.zezula.books.domain.*
import dev.zezula.books.ui.screen.create.CreateBookViewModel
import dev.zezula.books.ui.screen.detail.BookDetailViewModel
import dev.zezula.books.ui.screen.list.BookListViewModel
import dev.zezula.books.ui.screen.search.SearchBarcodeViewModel
import dev.zezula.books.ui.screen.shelves.ShelvesViewModel
import dev.zezula.books.ui.screen.signin.SignInViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

val appModule = module {

    // Network services
    single {
        Retrofit.Builder()
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .baseUrl("https://www.goodreads.com/")
            .build()
    }
    single<GoodreadsApi> { get<Retrofit>().create(GoodreadsApi::class.java) }
    single<OnlineBookFinderService> { OnlineBookFinderServiceImpl(get()) }
    single<AuthService> { AuthServiceImpl(Firebase.auth) }

    // Database and DAOs
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "app_database"
        )
            .build()

    }
    single {
        val database = get<AppDatabase>()
        database.bookDao()
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
    single<NetworkDataSource> {
        FirestoreDataSource()
    }

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