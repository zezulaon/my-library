package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.fromBookFormData
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

class BooksRepositoryImpl(
    private val bookDao: BookDao,
) : BooksRepository {

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
