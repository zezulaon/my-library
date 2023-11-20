package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.first
import timber.log.Timber

class FindBookForIsbnOnlineUseCase(
    private val booksRepository: BooksRepository,
    private val onlineBookFinderService: OnlineBookFinderService,
    private val userLibraryRepository: UserLibraryRepository,
) {

    suspend operator fun invoke(isbn: String): Response<String?> {
        return asResponse {
            findBook(isbn)
        }
            .onError {
                Timber.e(it, "Failed to search book for isbn: [$isbn].")
            }
    }

    private suspend fun findBook(isbn: String): String? {
        // Skips downloading if the book is already in the DB
        val existingId = booksRepository.getBookId(isbn)
        if (existingId != null) {
            if (userLibraryRepository.isBookInLibrary(existingId).first().not()) {
                userLibraryRepository.moveBookToLibrary(existingId)
            }
            return existingId
        }

        val bookFormData = onlineBookFinderService.findBookForIsbnOnline(isbn)
        return if (bookFormData != null) {
            val addedBook = userLibraryRepository.addBook(bookFormData)
            addedBook.id
        } else {
            null
        }
    }
}
