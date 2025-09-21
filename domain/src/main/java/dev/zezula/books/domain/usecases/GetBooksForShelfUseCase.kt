package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.domain.repositories.SortBooksBy
import dev.zezula.books.domain.repositories.UserLibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetBooksForShelfUseCase(private val userLibraryRepository: UserLibraryRepository) {

    operator fun invoke(selectedShelf: Shelf?, sortBooksBy: SortBooksBy): Flow<Response<List<Book>>> {
        val booksFlow = if (selectedShelf == null) {
            userLibraryRepository.getAllLibraryBooksFlow()
        } else {
            userLibraryRepository.getAllBooksForShelfStream(selectedShelf.id)
        }
        return booksFlow.map { books ->
            val sortedBooks = when (sortBooksBy) {
                SortBooksBy.DATE_ADDED -> books
                SortBooksBy.TITLE -> books.sortedBy { it.titleWithoutArticle }
                SortBooksBy.AUTHOR -> books.sortedBy { it.authorLastName }
                SortBooksBy.USER_RATING -> books.sortedByDescending { it.userRating }
            }
            sortedBooks
        }
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load books for shelf: ${selectedShelf?.id}")
            }
    }
}