package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.BooksRepository
import dev.zezula.books.domain.repositories.UserLibraryRepository
import dev.zezula.books.domain.services.OnlineBookFinderService
import kotlinx.coroutines.flow.first
import timber.log.Timber

class FindBookForIsbnOnlineUseCase(
    private val booksRepository: BooksRepository,
    private val onlineBookFinderService: OnlineBookFinderService,
    private val userLibraryRepository: UserLibraryRepository,
) {

    suspend operator fun invoke(isbn: String): Response<Book.Id?> {
        return asResponse {
            findBook(isbn)
        }
            .onError {
                Timber.e(it, "Failed to search book for isbn: [$isbn].")
            }
    }

    private suspend fun findBook(isbn: String): Book.Id? {
        // Skips downloading if the book is already in the DB
        val existingBooks = booksRepository.getBooksByIsbn(isbn)
        if (existingBooks.isNotEmpty()) {
            val existingBookId = existingBooks.first().id
            if (userLibraryRepository.isBookInLibrary(existingBookId).first().not()) {
                userLibraryRepository.moveExistingBookToLibrary(existingBookId)
            }
            return existingBookId
        }

        val bookFormData = onlineBookFinderService.findBookForIsbnOnline(isbn)
        return if (bookFormData != null) {
            val addedBookId = userLibraryRepository.addBookToLibrary(bookFormData)
            addedBookId
        } else {
            null
        }
    }
}
