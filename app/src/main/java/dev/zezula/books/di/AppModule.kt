package dev.zezula.books.di

import dev.zezula.books.BuildConfig
import dev.zezula.books.core.utils.di.IO_SCOPE
import dev.zezula.books.data.di.dataModule
import dev.zezula.books.legacy.MigrationConfigDataProvider
import dev.zezula.books.ui.screen.appinfo.AppInfoViewModel
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
import di.domainModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    includes(domainModule, dataModule)

    factory<CoroutineScope>(qualifier = named(IO_SCOPE)) {
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    factory<MigrationConfigDataProvider> {
        object : MigrationConfigDataProvider {
            override fun getApplicationId(): String = BuildConfig.APPLICATION_ID
            override fun getVersionCode(): String = BuildConfig.VERSION_CODE.toString()
        }
    }

    // ViewModels
    viewModel { BookListViewModel(get(), get(), get(), get()) }
    viewModel { ShelvesViewModel(get(), get(), get(), get()) }
    viewModel { AllAuthorsViewModel(get()) }
    viewModel { AuthorBooksViewModel(get(), get()) }
    viewModel { CreateBookViewModel(get(), get(), get()) }
    viewModel { BookDetailViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { EmailSignInViewModel(get()) }
    viewModel { FindBookViewModel(get()) }
    viewModel { SearchMyLibraryViewModel(get()) }
    viewModel { AllNotesViewModel(get()) }
    viewModel { SearchBarcodeViewModel(get(), get(), get(), get(), get()) }
    viewModel { AppInfoViewModel(get(), get(), get()) }
}
