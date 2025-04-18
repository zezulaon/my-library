package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.SearchBookResultEntity
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.toBookEntity
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.SearchBookResultDao
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
