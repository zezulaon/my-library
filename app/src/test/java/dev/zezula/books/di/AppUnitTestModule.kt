package dev.zezula.books.di

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
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.RatingDao
import dev.zezula.books.data.source.db.ReviewDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.fake.FakeBookDaoImpl
import dev.zezula.books.data.source.db.fake.FakeMyLibraryApi
import dev.zezula.books.data.source.db.fake.FakeNoteDaoImpl
import dev.zezula.books.data.source.db.fake.FakeRatingDaoImpl
import dev.zezula.books.data.source.db.fake.FakeReviewDaoImpl
import dev.zezula.books.data.source.db.fake.FakeShelfAndBookDaoImpl
import dev.zezula.books.data.source.network.MyLibraryApi
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.data.source.network.fake.FakeNetworkDataSourceImpl
import dev.zezula.books.data.source.network.fake.FakeOnlineBookFinderServiceImpl
import dev.zezula.books.domain.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.domain.CheckReviewsDownloadedUseCase
import dev.zezula.books.domain.CreateNoteUseCase
import dev.zezula.books.domain.CreateShelfUseCase
import dev.zezula.books.domain.DeleteBookFromLibraryUseCase
import dev.zezula.books.domain.DeleteNoteUseCase
import dev.zezula.books.domain.DeleteShelfUseCase
import dev.zezula.books.domain.FetchSuggestionsUseCase
import dev.zezula.books.domain.FindBookForIsbnOnlineUseCase
import dev.zezula.books.domain.GetAllAuthorsUseCase
import dev.zezula.books.domain.GetAllBookDetailUseCase
import dev.zezula.books.domain.GetBookUseCase
import dev.zezula.books.domain.GetBooksForAuthorUseCase
import dev.zezula.books.domain.GetBooksForShelfUseCase
import dev.zezula.books.domain.GetShelvesUseCase
import dev.zezula.books.domain.MoveBookToLibraryUseCase
import dev.zezula.books.domain.RefreshBookCoverUseCase
import dev.zezula.books.domain.RefreshLibraryUseCase
import dev.zezula.books.domain.ToggleBookInShelfUseCase
import dev.zezula.books.domain.UpdateLastSignedInDateUseCase
import dev.zezula.books.domain.UpdateShelfUseCase
import dev.zezula.books.ui.screen.authors.AllAuthorsViewModel
import dev.zezula.books.ui.screen.authors.AuthorBooksViewModel
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
    single<MyLibraryApi> { FakeMyLibraryApi() }

    single<BookDao> { FakeBookDaoImpl() }
    single<NoteDao> { FakeNoteDaoImpl() }
    single<RatingDao> { FakeRatingDaoImpl() }
    single<ReviewDao> { FakeReviewDaoImpl() }
    single<ShelfAndBookDao> { FakeShelfAndBookDaoImpl() }

    single<NetworkDataSource> { FakeNetworkDataSourceImpl() }

    // UseCases
    single { GetBooksForShelfUseCase(get()) }
    single { RefreshLibraryUseCase(get()) }
    single { GetShelvesUseCase(get()) }
    single { FetchSuggestionsUseCase(get()) }
    single { DeleteShelfUseCase(get()) }
    single { UpdateShelfUseCase(get()) }
    single { CreateShelfUseCase(get()) }
    single { CreateNoteUseCase(get()) }
    single { DeleteNoteUseCase(get()) }
    single { GetAllBookDetailUseCase(get(), get(), get(), get(), get(), get()) }
    single { DeleteBookFromLibraryUseCase(get()) }
    single { ToggleBookInShelfUseCase(get()) }
    single { CheckReviewsDownloadedUseCase(get(), get()) }
    single { FindBookForIsbnOnlineUseCase(get(), get(), get()) }
    single { GetBookUseCase(get()) }
    single { AddOrUpdateLibraryBookUseCase(get()) }
    single { UpdateLastSignedInDateUseCase(get()) }
    single { MoveBookToLibraryUseCase(get()) }
    single { GetAllAuthorsUseCase(get()) }
    single { GetBooksForAuthorUseCase(get()) }
    single { RefreshBookCoverUseCase(get(), get(), get()) }

    // Repositories
    single<BooksRepository> { BooksRepositoryImpl(get()) }
    single<UserLibraryRepository> { UserLibraryRepositoryImpl(get(), get(), get()) }
    single<NotesRepository> { NotesRepositoryImpl(get()) }
    single<ShelvesRepository> { ShelvesRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl() }
    single<ReviewsRepository> { ReviewsRepositoryImpl(get(), get(), get(), get()) }

    // ViewModels
    viewModel { BookListViewModel(get(), get(), get(), get(), get()) }
    viewModel { ShelvesViewModel(get(), get(), get(), get()) }
    viewModel { AllAuthorsViewModel(get()) }
    viewModel { AuthorBooksViewModel(get(), get()) }
    viewModel { CreateBookViewModel(get(), get(), get()) }
    viewModel { BookDetailViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { SearchBarcodeViewModel(get(), get(), get(), get(), get()) }
}
