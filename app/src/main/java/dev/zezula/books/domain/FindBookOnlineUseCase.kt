package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.ReviewsRepository
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class FindBookOnlineUseCase(
    private val booksRepository: BooksRepository,
    private val reviewsRepository: ReviewsRepository,
    private val onlineBookFinderService: OnlineBookFinderService,
) {

    suspend operator fun invoke(isbn: String): Response<String?> {
        return asResponse {
            getBook(isbn)
        }
            .onError {
                Timber.e(it, "Failed to search book for isbn: [$isbn].")
            }
    }

    private suspend fun getBook(isbn: String): String? {
        // Skips downloading if the book is already in the DB
        val existingId = booksRepository.getBookId(isbn)
        if (existingId != null) return existingId

        val response = onlineBookFinderService.findBookOnline(isbn)
        val addedBook = booksRepository.addBook(response)
        if (addedBook != null) {
            reviewsRepository.addReviews(addedBook, response)
        }

        return addedBook?.id
    }
}