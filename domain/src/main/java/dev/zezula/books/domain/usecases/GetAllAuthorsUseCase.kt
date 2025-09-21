package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.AuthorAndBooks
import dev.zezula.books.core.utils.splitToAuthors
import dev.zezula.books.core.utils.toAuthorNameId
import dev.zezula.books.core.utils.toSortingAuthor
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.domain.repositories.UserLibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetAllAuthorsUseCase(private val userLibraryRepository: UserLibraryRepository) {

    /**
     * Returns a list of all authors with the number of their books.
     */
    operator fun invoke(): Flow<Response<List<AuthorAndBooks>>> {
        val booksFlow = userLibraryRepository.getAllLibraryBooksFlow()
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