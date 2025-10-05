package di

import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.domain.usecases.CheckReviewsDownloadedUseCase
import dev.zezula.books.domain.usecases.CreateNoteUseCase
import dev.zezula.books.domain.usecases.CreateShelfUseCase
import dev.zezula.books.domain.usecases.DeleteBookFromLibraryUseCase
import dev.zezula.books.domain.usecases.DeleteNoteUseCase
import dev.zezula.books.domain.usecases.DeleteShelfUseCase
import dev.zezula.books.domain.usecases.FetchSuggestionsUseCase
import dev.zezula.books.domain.usecases.FindBookForIsbnOnlineUseCase
import dev.zezula.books.domain.usecases.FindBookForQueryOnlineUseCase
import dev.zezula.books.domain.usecases.GetAllAuthorsUseCase
import dev.zezula.books.domain.usecases.GetAllBookDetailUseCase
import dev.zezula.books.domain.usecases.GetAllNotesUseCase
import dev.zezula.books.domain.usecases.GetBookUseCase
import dev.zezula.books.domain.usecases.GetBooksForAuthorUseCase
import dev.zezula.books.domain.usecases.GetBooksForShelfUseCase
import dev.zezula.books.domain.usecases.GetShelvesUseCase
import dev.zezula.books.domain.usecases.MoveBookToLibraryUseCase
import dev.zezula.books.domain.usecases.RefreshBookCoverUseCase
import dev.zezula.books.domain.usecases.SearchMyLibraryBooksUseCase
import dev.zezula.books.domain.usecases.ToggleBookInShelfUseCase
import dev.zezula.books.domain.usecases.UpdateLastSignedInDateUseCase
import dev.zezula.books.domain.usecases.UpdateNoteUseCase
import dev.zezula.books.domain.usecases.UpdateShelfUseCase
import dev.zezula.books.domain.usecases.export.ExportLibraryUseCase
import dev.zezula.books.domain.usecases.export.GetExportDirUseCase
import dev.zezula.books.domain.usecases.export.LastExportedFilesUseCase
import org.koin.dsl.module

val domainModule = module {

    // UseCases
    single { GetBooksForShelfUseCase(get()) }
    single { FetchSuggestionsUseCase(get()) }
    single { GetShelvesUseCase(get()) }
    single { DeleteShelfUseCase(get()) }
    single { CreateNoteUseCase(get()) }
    single { UpdateNoteUseCase(get()) }
    single { DeleteNoteUseCase(get()) }
    single { UpdateShelfUseCase(get()) }
    single { CreateShelfUseCase(get()) }
    single { GetAllBookDetailUseCase(get(), get(), get(), get(), get(), get()) }
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
    factory { ExportLibraryUseCase(get(), get(), get(), get()) }
    factory { GetExportDirUseCase(get()) }
    factory { LastExportedFilesUseCase(get()) }
    single { RefreshBookCoverUseCase(get(), get()) }
}
