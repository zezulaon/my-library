package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class BooksRepositoryImpl(
    private val bookDao: BookDao,
    private val shelfAndBookDao: ShelfAndBookDao,
    private val noteDao: NoteDao,
) : BooksRepository {

    override fun getBookFlow(bookId: Book.Id): Flow<Book?> {
        return bookDao.getBookFlow(bookId)
            .map {
                it?.asExternalModel()
            }
    }

    override suspend fun getBook(bookId: Book.Id): Book? {
        return getBookFlow(bookId).first()
    }

    override suspend fun getBooksByIsbn(isbn: String): List<Book> {
        return bookDao
            .getBooksByIsbn(isbn)
            .map { it.asExternalModel() }
    }

    override suspend fun updateBookCover(bookId: Book.Id, thumbnailLink: String) {
        bookDao.updateBookCover(bookId = bookId, thumbnailLink = thumbnailLink, lastModifiedTimestamp = Clock.System.now().toString())
    }

    override suspend fun softDeleteBook(bookId: Book.Id) {
        bookDao.softDeleteBook(bookId = bookId, lastModifiedTimestamp = Clock.System.now().toString())
        shelfAndBookDao.softDeleteShelvesWithBooksForBook(bookId = bookId, lastModifiedTimestamp = Clock.System.now().toString())
        noteDao.softDeleteNotesForBook(
            bookId = bookId,
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
    }
}
