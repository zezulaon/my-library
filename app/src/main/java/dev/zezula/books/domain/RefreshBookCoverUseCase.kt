package dev.zezula.books.domain

import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

// TODO: Refactor into more general Refresh/Update book data use case.
class RefreshBookCoverUseCase(
    private val bookDao: BookDao,
    private val networkDataSource: NetworkDataSource,
    private val onlineBookFinderService: OnlineBookFinderService,
) {

    suspend operator fun invoke(bookId: String): Response<Unit> {
        return asResponse {
            updateBookCover(bookId)
        }
            .onError {
                Timber.e(it, "Failed to update book: [$bookId] cover.")
            }
    }

    private suspend fun updateBookCover(bookId: String) {
        val book = bookDao.getBookStream(bookId).firstOrNull()
        if (book?.isbn != null && book.thumbnailLink == null) {
            val isbn = book.isbn
            val thumbnailLink = onlineBookFinderService.findBookCoverLinkForIsbn(isbn)
            if (thumbnailLink != null) {
                bookDao.updateBookCover(book.id, thumbnailLink)
                networkDataSource.updateBookCover(book.id, thumbnailLink)
            }
        }
    }
}
