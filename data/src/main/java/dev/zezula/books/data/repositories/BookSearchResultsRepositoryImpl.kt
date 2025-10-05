package dev.zezula.books.data.repositories

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.data.database.BookDao
import dev.zezula.books.data.database.SearchBookResultDao
import dev.zezula.books.data.database.entities.BookEntity
import dev.zezula.books.data.database.entities.SearchBookResultEntity
import dev.zezula.books.data.database.entities.asExternalModel
import dev.zezula.books.data.toBookEntity
import dev.zezula.books.domain.repositories.BookSearchResultsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class BookSearchResultsRepositoryImpl(
    private val bookDao: BookDao,
    private val searchBookResultDao: SearchBookResultDao,
) : BookSearchResultsRepository {

    override fun getAllSearchResultsFlow(): Flow<List<Book>> {
        return searchBookResultDao.getAllSearchResultBooksFlow().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun addBookToSearchResults(bookFormData: BookFormData) {
        val bookId = Book.Id(UUID.randomUUID().toString())
        val bookEntity = bookFormData.toBookEntity(
            id = bookId,
            dateAdded = LocalDateTime.now().toString(),
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
        bookDao.insertBook(bookEntity)
        searchBookResultDao.insertSearchBookResults(SearchBookResultEntity(bookId = bookId))
    }

    override suspend fun deleteAllSearchResults() {
        Timber.d("deleteAllSearchBookResults()")
        searchBookResultDao.deleteAllSearchResults()
    }
}
