package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

// TODO: Refactor into more general Refresh/Update book data use case.
class RefreshBookCoverUseCase(
    private val booksRepository: BooksRepository,
    private val onlineBookFinderService: OnlineBookFinderService,
) {

    suspend operator fun invoke(bookId: Book.Id): Response<Unit> {
        return asResponse {
            updateBookCover(bookId)
        }
            .onError {
                Timber.e(it, "Failed to update book: [$bookId] cover.")
            }
    }

    private suspend fun updateBookCover(bookId: Book.Id) {
        val book = booksRepository.getBookFlow(bookId).firstOrNull()
        if (book?.isbn != null && book.thumbnailLink == null) {
            val isbn = book.isbn
            val thumbnailLink = onlineBookFinderService.findBookCoverLinkForIsbn(isbn)
            if (thumbnailLink != null) {
                booksRepository.updateBookCover(bookId = book.id, thumbnailLink = thumbnailLink)
            }
        }
    }
}
