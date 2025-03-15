package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class BooksRepositoryImpl(
    private val bookDao: BookDao,
) : BooksRepository {

    override fun getBookStream(bookId: String): Flow<Book?> {
        return bookDao.getBookStream(bookId)
            .map {
                it?.asExternalModel()
            }
    }

    override suspend fun getBook(bookId: String): Book? {
        return getBookStream(bookId).first()
    }

    override suspend fun getBooksByIsbn(isbn: String): List<Book> {
        return bookDao
            .getBooksByIsbn(isbn)
            .map { it.asExternalModel() }
    }
}
