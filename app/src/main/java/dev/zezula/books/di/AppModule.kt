package dev.zezula.books.di

import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.zezula.books.BuildConfig
import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.BooksRepositoryImpl
import dev.zezula.books.data.NotesRepository
import dev.zezula.books.data.NotesRepositoryImpl
import dev.zezula.books.data.ReviewsRepository
import dev.zezula.books.data.ReviewsRepositoryImpl
import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.data.ShelvesRepositoryImpl
import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.data.UserLibraryRepositoryImpl
import dev.zezula.books.data.UserRepository
import dev.zezula.books.data.UserRepositoryImpl
import dev.zezula.books.data.source.db.AppDatabase
import dev.zezula.books.data.source.db.MIGRATION_3_4
import dev.zezula.books.data.source.db.legacy.LegacyAppDatabase
import dev.zezula.books.data.source.network.AuthService
import dev.zezula.books.data.source.network.AuthServiceImpl
import dev.zezula.books.data.source.network.FirestoreDataSource
import dev.zezula.books.data.source.network.GoodreadsApi
import dev.zezula.books.data.source.network.GoogleApi
import dev.zezula.books.data.source.network.MyLibraryApi
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.data.source.network.OnlineBookFinderServiceImpl
import dev.zezula.books.data.source.network.OpenLibraryApi
import dev.zezula.books.domain.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.domain.CheckReviewsDownloadedUseCase
import dev.zezula.books.domain.CreateOrUpdateNoteUseCase
import dev.zezula.books.domain.CreateShelfUseCase
import dev.zezula.books.domain.DeleteBookFromLibraryUseCase
import dev.zezula.books.domain.DeleteNoteUseCase
import dev.zezula.books.domain.DeleteShelfUseCase
import dev.zezula.books.domain.FetchSuggestionsUseCase
import dev.zezula.books.domain.FindBookForIsbnOnlineUseCase
import dev.zezula.books.domain.FindBookForQueryOnlineUseCase
import dev.zezula.books.domain.GetAllAuthorsUseCase
import dev.zezula.books.domain.GetAllBookDetailUseCase
import dev.zezula.books.domain.GetAllNotesUseCase
import dev.zezula.books.domain.GetBookUseCase
import dev.zezula.books.domain.GetBooksForAuthorUseCase
import dev.zezula.books.domain.GetBooksForShelfUseCase
import dev.zezula.books.domain.GetShelvesUseCase
import dev.zezula.books.domain.MoveBookToLibraryUseCase
import dev.zezula.books.domain.CheckMigrationUseCase
import dev.zezula.books.domain.RefreshLibraryUseCase
import dev.zezula.books.domain.SearchMyLibraryBooksUseCase
import dev.zezula.books.domain.ToggleBookInShelfUseCase
import dev.zezula.books.domain.UpdateLastSignedInDateUseCase
import dev.zezula.books.domain.UpdateShelfUseCase
import dev.zezula.books.ui.screen.authors.AllAuthorsViewModel
import dev.zezula.books.ui.screen.authors.AuthorBooksViewModel
import dev.zezula.books.ui.screen.create.CreateBookViewModel
import dev.zezula.books.ui.screen.detail.BookDetailViewModel
import dev.zezula.books.ui.screen.list.BookListViewModel
import dev.zezula.books.ui.screen.notes.AllNotesViewModel
import dev.zezula.books.ui.screen.search.FindBookViewModel
import dev.zezula.books.ui.screen.search.SearchBarcodeViewModel
import dev.zezula.books.ui.screen.search.SearchMyLibraryViewModel
import dev.zezula.books.ui.screen.shelves.ShelvesViewModel
import dev.zezula.books.ui.screen.signin.EmailSignInViewModel
import dev.zezula.books.ui.screen.signin.SignInViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    // Network services
    single<GoodreadsApi> {
        // SimpleXmlConverterFactory is deprecated but working. There seems to be no alternative for Android right now:
        // https://github.com/square/retrofit/issues/2733
        @Suppress("DEPRECATION")
        val create = retrofit2.converter.simplexml.SimpleXmlConverterFactory.create()

        Retrofit.Builder()
            .addConverterFactory(create)
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
    single<MyLibraryApi> {

        val clientBuilder = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.ML_BASE_API_URL)
            .client(clientBuilder.build())
            .build()
            .create(MyLibraryApi::class.java)
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
            .addMigrations(MIGRATION_3_4)
            .build()
    }
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
    single {
        val database = get<LegacyAppDatabase>()
        database.legacyShelfDao()
    }
    single {
        val database = get<LegacyAppDatabase>()
        database.legacyGroupShelfBookDao()
    }
    single {
        val database = get<AppDatabase>()
        database.bookDao()
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
        database.shelfAndBookDao()
    }
    single<NetworkDataSource> {
        FirestoreDataSource()
    }

    // UseCases
    single { GetBooksForShelfUseCase(get()) }
    single { RefreshLibraryUseCase(get()) }
    single { FetchSuggestionsUseCase(get()) }
    single { GetShelvesUseCase(get()) }
    single { DeleteShelfUseCase(get()) }
    single { CreateOrUpdateNoteUseCase(get()) }
    single { DeleteNoteUseCase(get()) }
    single { UpdateShelfUseCase(get()) }
    single { CreateShelfUseCase(get()) }
    single { GetAllBookDetailUseCase(get(), get(), get(), get(), get()) }
    single { DeleteBookFromLibraryUseCase(get()) }
    single { ToggleBookInShelfUseCase(get()) }
    single { CheckReviewsDownloadedUseCase(get(), get()) }
    single { FindBookForIsbnOnlineUseCase(get(), get(), get()) }
    single { FindBookForQueryOnlineUseCase(get(), get()) }
    single { SearchMyLibraryBooksUseCase(get()) }
    single { GetBookUseCase(get()) }
    single { AddOrUpdateLibraryBookUseCase(get()) }
    single { MoveBookToLibraryUseCase(get()) }
    single { UpdateLastSignedInDateUseCase(get()) }
    single { GetAllAuthorsUseCase(get()) }
    single { GetAllNotesUseCase(get()) }
    single { GetBooksForAuthorUseCase(get()) }
    single { CheckMigrationUseCase(get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    // Repositories
    single<BooksRepository> { BooksRepositoryImpl(get(), get()) }
    single<UserLibraryRepository> { UserLibraryRepositoryImpl(get(), get(), get(), get()) }
    single<NotesRepository> { NotesRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl() }
    single<ShelvesRepository> { ShelvesRepositoryImpl(get(), get()) }
    single<ReviewsRepository> { ReviewsRepositoryImpl(get(), get(), get(), get()) }

    // ViewModels
    viewModel { BookListViewModel(get(), get(), get(), get()) }
    viewModel { ShelvesViewModel(get(), get(), get(), get()) }
    viewModel { AllAuthorsViewModel(get()) }
    viewModel { AuthorBooksViewModel(get(), get()) }
    viewModel { CreateBookViewModel(get(), get(), get()) }
    viewModel { BookDetailViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { EmailSignInViewModel(get()) }
    viewModel { FindBookViewModel(get()) }
    viewModel { SearchMyLibraryViewModel(get()) }
    viewModel { AllNotesViewModel(get()) }
    viewModel { SearchBarcodeViewModel(get(), get(), get(), get(), get()) }
}
