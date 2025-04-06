package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.BookSuggestionEntity
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.toBookEntity
import dev.zezula.books.data.model.openLibrary.toBookFormData
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.BookSuggestionDao
import dev.zezula.books.data.source.network.OpenLibraryApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class BookSuggestionsRepositoryImpl(
    private val bookDao: BookDao,
    private val bookSuggestionDao: BookSuggestionDao,
    private val openLibraryApi: OpenLibraryApi,
) : BookSuggestionsRepository {

    override fun getAllSuggestionsForBookFlow(bookId: Book.Id): Flow<List<Book>> {
        return bookSuggestionDao.getAllSuggestionsForBookFlow(bookId).map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun fetchSuggestions(bookId: Book.Id): List<Book> {
        val parentBook = bookDao.getBookFlow(bookId).firstOrNull()

        val resultBookFormData = mutableListOf<BookFormData>()
        resultBookFormData.addAll(suggestionsByAuthor(parentBook))

        resultBookFormData.forEach { bookFormData ->
            val entity = bookFormData.toBookEntity(
                id = Book.Id(UUID.randomUUID().toString()),
                dateAdded = LocalDateTime.now().toString(),
                lastModifiedTimestamp = Clock.System.now().toString(),
            )

            bookDao.insertBook(entity)
            bookSuggestionDao.addToBookSuggestions(
                BookSuggestionEntity(
                    bookId = entity.id,
                    parentBookId = bookId,
                ),
            )
        }

        return getAllSuggestionsForBookFlow(bookId).first()
    }

    private suspend fun suggestionsByAuthor(
        parentBook: BookEntity?,
    ): Collection<BookFormData> {
        val author = parentBook?.author
        if (author != null) {
            val topRatedByAuthorResponse = openLibraryApi.searchByQuery(author = author, limit = 15)
            val topRatedByAuthorBooks: Collection<BookFormData> = topRatedByAuthorResponse?.docs?.map { it.toBookFormData() } ?: emptyList()
            return topRatedByAuthorBooks
        } else {
            Timber.d("Cannot fetch suggestions for book: ${parentBook?.id} because it is missing author.")
            return emptyList()
        }
    }
}
