package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.SearchBookResultEntity
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class BookSearchResultsRepositoryImpl(
    private val bookDao: BookDao,
) : BookSearchResultsRepository {

    override fun getAllSearchResultsStream(): Flow<List<Book>> {
        return bookDao.getAllSearchResultBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun addBookToSearchResults(bookId: String) {
        bookDao.addToSearchBookResults(SearchBookResultEntity(bookId = bookId))
    }

    override suspend fun deleteAllSearchResults() {
        Timber.d("deleteAllSearchBookResults()")
        bookDao.deleteAllSearchResults()
    }
}
