package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.SearchBookResultEntity
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.fromBookFormData
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class BookSearchResultsRepositoryImpl(
    private val bookDao: BookDao,
) : BookSearchResultsRepository {

    override fun getAllSearchResultsStream(): Flow<List<Book>> {
        return bookDao.getAllSearchResultBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun addBookToSearchResults(bookFormData: BookFormData): Book {
        val book =  addBook(bookFormData)
        bookDao.addToSearchBookResults(SearchBookResultEntity(bookId = book.id))
        return book
    }

    // FIXME: find out if this (simple book insertion) is duplicated elsewhere
    private suspend fun addBook(bookFormData: BookFormData): Book {
        val createdId = UUID.randomUUID().toString()
        val bookEntity = fromBookFormData(
            id = createdId,
            dateAdded = bookFormData.dateAdded ?: LocalDateTime.now().toString(),
            bookFormData = bookFormData,
        )
        bookDao.addOrUpdate(bookEntity)
        return bookEntity.asExternalModel()
    }

    override suspend fun deleteAllSearchResults() {
        Timber.d("deleteAllSearchBookResults()")
        bookDao.deleteAllSearchResults()
    }
}
