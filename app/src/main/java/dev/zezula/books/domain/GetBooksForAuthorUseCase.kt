package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.util.splitToAuthors
import dev.zezula.books.util.toAuthorNameId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetBooksForAuthorUseCase(private val repository: BooksRepository) {

    /**
     * Returns a list of books for the given authorNameId where the authorNameId is the author name without spaces and
     * in lower case.
     */
    operator fun invoke(authorNameId: String): Flow<Response<List<Book>>> {
        return repository
            .getAllBooksStream()
            .map { books ->
                // Filter the books to only those that have the authorNameId in the list of authors
                books.filter { book ->
                    // Check if the authorNameId is in the list of authors for the book
                    val authors = book.author?.splitToAuthors() ?: emptyList()
                    authors.any { it.toAuthorNameId() == authorNameId }
                }
            }
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load books for author: $authorNameId")
            }
    }
}