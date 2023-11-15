package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.ui.screen.authors.AuthorAndBooks
import dev.zezula.books.util.splitToAuthors
import dev.zezula.books.util.toAuthorNameId
import dev.zezula.books.util.toSortingAuthor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetAllAuthorsUseCase(private val repository: BooksRepository) {

    /**
     * Returns a list of all authors with the number of their books.
     */
    operator fun invoke(): Flow<Response<List<AuthorAndBooks>>> {
        val booksFlow = repository.getAllLibraryBooksStream()
        // Maps the list of books to a list of all authors with the number of books
        return booksFlow.map { books ->
            val idToAuthorMap = mutableMapOf<String, AuthorAndBooks>()
            books.forEach { book ->
                book.author?.let { author ->
                    val authors = author.splitToAuthors()
                    authors.forEach { authorName ->
                        val authorNameId = authorName.toAuthorNameId()
                        val authorAndBooks = idToAuthorMap[authorNameId]
                        if (authorAndBooks == null) {
                            idToAuthorMap[authorNameId] = AuthorAndBooks(
                                authorNameId = authorNameId,
                                authorName = authorName,
                                numberOfBooks = 1,
                            )
                        } else {
                            authorAndBooks.numberOfBooks++
                        }
                    }
                }
            }
            idToAuthorMap.values.toList().sortedBy { it.authorName.toSortingAuthor() }
        }
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load all authors")
            }
    }
}
