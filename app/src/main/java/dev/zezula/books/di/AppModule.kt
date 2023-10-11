package dev.zezula.books.di

import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.BooksRepositoryImpl
import dev.zezula.books.data.ReviewsRepository
import dev.zezula.books.data.ReviewsRepositoryImpl
import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.data.ShelvesRepositoryImpl
import dev.zezula.books.data.source.db.AppDatabase
import dev.zezula.books.data.source.network.AuthService
import dev.zezula.books.data.source.network.AuthServiceImpl
import dev.zezula.books.data.source.network.FirestoreDataSource
import dev.zezula.books.data.source.network.GoodreadsApi
import dev.zezula.books.data.source.network.GoogleApi
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.data.source.network.OnlineBookFinderServiceImpl
import dev.zezula.books.data.source.network.OpenLibraryApi
import dev.zezula.books.domain.AddOrUpdateBookUseCase
import dev.zezula.books.domain.CheckReviewsDownloadedUseCase
import dev.zezula.books.domain.CreateOrUpdateNoteUseCase
import dev.zezula.books.domain.CreateShelfUseCase
import dev.zezula.books.domain.DeleteBookUseCase
import dev.zezula.books.domain.DeleteNoteUseCase
import dev.zezula.books.domain.DeleteShelfUseCase
import dev.zezula.books.domain.FindBookForIsbnOnlineUseCase
import dev.zezula.books.domain.FindBookForQueryOnlineUseCase
import dev.zezula.books.domain.GetAllAuthorsUseCase
import dev.zezula.books.domain.GetAllBookDetailUseCase
import dev.zezula.books.domain.GetBooksForAuthorUseCase
import dev.zezula.books.domain.GetBooksForShelfUseCase
import dev.zezula.books.domain.GetBooksUseCase
import dev.zezula.books.domain.GetShelvesUseCase
import dev.zezula.books.domain.RefreshLibraryUseCase
import dev.zezula.books.domain.SearchMyLibraryBooksUseCase
import dev.zezula.books.domain.ToggleBookInShelfUseCase
import dev.zezula.books.domain.UpdateShelfUseCase
import dev.zezula.books.ui.screen.authors.AllAuthorsViewModel
import dev.zezula.books.ui.screen.authors.AuthorBooksViewModel
import dev.zezula.books.ui.screen.create.CreateBookViewModel
import dev.zezula.books.ui.screen.detail.BookDetailViewModel
import dev.zezula.books.ui.screen.list.BookListViewModel
import dev.zezula.books.ui.screen.search.FindBookViewModel
import dev.zezula.books.ui.screen.search.SearchBarcodeViewModel
import dev.zezula.books.ui.screen.search.SearchMyLibraryViewModel
import dev.zezula.books.ui.screen.shelves.ShelvesViewModel
import dev.zezula.books.ui.screen.signin.EmailSignInViewModel
import dev.zezula.books.ui.screen.signin.SignInViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

val appModule = module {

    // Network services
    single<GoodreadsApi> {
        Retrofit.Builder()
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .baseUrl("https://www.goodreads.com/")
            .build()
            .create(GoodreadsApi::class.java)
    }
    single<OpenLibraryApi> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://openlibrary.org/")
            .build()
            .create(OpenLibraryApi::class.java)
    }
    single<GoogleApi> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.googleapis.com/")
            .build()
            .create(GoogleApi::class.java)
    }
    single<OnlineBookFinderService> { OnlineBookFinderServiceImpl(get(), get(), get()) }
    single<AuthService> { AuthServiceImpl(Firebase.auth) }

    // Database and DAOs
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "app_database",
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
        database.shelfAndBookDao()
    }
    single<NetworkDataSource> {
        FirestoreDataSource()
    }

    // UseCases
    single { GetBooksForShelfUseCase(get()) }
    single { RefreshLibraryUseCase(get()) }
    single { GetShelvesUseCase(get()) }
    single { DeleteShelfUseCase(get()) }
    single { CreateOrUpdateNoteUseCase(get()) }
    single { DeleteNoteUseCase(get()) }
    single { UpdateShelfUseCase(get()) }
    single { CreateShelfUseCase(get()) }
    single { GetAllBookDetailUseCase(get(), get(), get()) }
    single { DeleteBookUseCase(get()) }
    single { ToggleBookInShelfUseCase(get()) }
    single { CheckReviewsDownloadedUseCase(get(), get()) }
    single { FindBookForIsbnOnlineUseCase(get(), get()) }
    single { FindBookForQueryOnlineUseCase(get()) }
    single { SearchMyLibraryBooksUseCase(get()) }
    single { GetBooksUseCase(get()) }
    single { AddOrUpdateBookUseCase(get()) }
    single { GetAllAuthorsUseCase(get()) }
    single { GetBooksForAuthorUseCase(get()) }

    // Repositories
    single<BooksRepository> { BooksRepositoryImpl(get(), get(), get()) }
    single<ShelvesRepository> { ShelvesRepositoryImpl(get(), get()) }
    single<ReviewsRepository> { ReviewsRepositoryImpl(get(), get(), get()) }

    // ViewModels
    viewModel { BookListViewModel(get(), get(), get()) }
    viewModel { ShelvesViewModel(get(), get(), get(), get()) }
    viewModel { AllAuthorsViewModel(get()) }
    viewModel { AuthorBooksViewModel(get(), get()) }
    viewModel { CreateBookViewModel(get(), get(), get()) }
    viewModel { BookDetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { EmailSignInViewModel(get()) }
    viewModel { FindBookViewModel(get(), get()) }
    viewModel { SearchMyLibraryViewModel(get()) }
    viewModel { SearchBarcodeViewModel(get(), get()) }
    viewModel { SearchBarcodeViewModel(get(), get()) }
}
