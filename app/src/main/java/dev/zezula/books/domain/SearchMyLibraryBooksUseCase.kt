package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class SearchMyLibraryBooksUseCase(private val repository: BooksRepository) {

    suspend operator fun invoke(query: String): Response<List<Book>> {
        return asResponse {
            repository.searchMyLibraryBooks(query)
        }
            .onError {
                Timber.e(it, "Failed to search book for query: [$query].")
            }
    }
}
