package dev.zezula.books.domain

import dev.zezula.books.data.SortBooksBy
import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetBooksForShelfUseCase(private val userLibraryRepository: UserLibraryRepository) {

    operator fun invoke(selectedShelf: Shelf?, sortBooksBy: SortBooksBy): Flow<Response<List<Book>>> {
        val booksFlow = if (selectedShelf == null) {
            userLibraryRepository.getAllLibraryBooksStream()
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
