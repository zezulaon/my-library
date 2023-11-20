package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.SearchBookResultEntity
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.fromBookFormData
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class BooksRepositoryImpl(
    private val bookDao: BookDao,
) : BooksRepository {

    override suspend fun addBookToSearchResults(bookId: String) {
        bookDao.addToSearchBookResults(SearchBookResultEntity(bookId = bookId))
    }

    override fun getAllSearchResultBooksStream(): Flow<List<Book>> {
        return bookDao.getAllSearchResultBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun deleteAllSearchBookResults() {
        Timber.d("deleteAllSearchBookResults()")
        val allSearchResults = bookDao.getAllSearchResultBooksStream().first()
        Timber.d("${allSearchResults.size} search results to delete.")
        allSearchResults.forEach { bookEntity ->
            val libraryBook = bookDao.getLibraryBookStream(bookEntity.id).first()
            if (libraryBook == null) {
                Timber.d("Deleting book: $bookEntity")
                bookDao.delete(bookEntity.id)
            } else {
                Timber.d("Not deleting book: $bookEntity because it is already in the user library.")
            }
        }
        bookDao.deleteAllSearchBookResults()
    }

    override fun getAllBooksStream(): Flow<List<Book>> {
        return bookDao.getAllBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getBookStream(bookId: String): Flow<Book?> {
        return bookDao.getBookStream(bookId)
            .map {
                it?.asExternalModel()
            }
    }

    override suspend fun addBook(bookFormData: BookFormData): Book {
        val createdId = UUID.randomUUID().toString()
        val bookEntity = fromBookFormData(
            id = createdId,
            dateAdded = bookFormData.dateAdded ?: LocalDateTime.now().toString(),
            bookFormData = bookFormData,
        )
        bookDao.addOrUpdate(bookEntity)
        return bookEntity.asExternalModel()
    }

    override suspend fun updateBook(bookId: String, bookFormData: BookFormData): Book {
        val dateAdded = checkNotNull(bookFormData.dateAdded) { "Cannot update book -> dateAdded must not be null." }
        val bookEntity = fromBookFormData(
            id = bookId,
            dateAdded = dateAdded,
            bookFormData = bookFormData,
        )
        bookDao.addOrUpdate(bookEntity)
        return bookEntity.asExternalModel()
    }

    override suspend fun getBook(bookId: String): Book? {
        return getBookStream(bookId).first()
    }

    override suspend fun getBookId(isbn: String): String? {
        val dbBooks = bookDao.getForIsbn(isbn)
        return dbBooks.firstOrNull()?.id
    }

    override suspend fun deleteBook(bookId: String) {
        bookDao.delete(bookId)
    }
}
