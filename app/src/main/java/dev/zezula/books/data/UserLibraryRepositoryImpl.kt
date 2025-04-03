package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.toBookEntity
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class UserLibraryRepositoryImpl(
    private val bookDao: BookDao,
    private val shelfAndBookDao: ShelfAndBookDao,
) : UserLibraryRepository {

    override fun getAllLibraryBooksStream(): Flow<List<Book>> {
        return bookDao.getAllLibraryBooksStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun moveExistingBookToLibrary(bookId: Book.Id) {
        bookDao.addToLibraryBooks(bookId = bookId, dateAdded = LocalDateTime.now().toString(), lastModifiedTimestamp = Clock.System.now().toString())
    }

    override fun isBookInLibrary(bookId: Book.Id): Flow<Boolean> {
        return bookDao.isBookInLibrary(bookId)
    }

    override fun getAllBooksForShelfStream(shelfId: Shelf.Id): Flow<List<Book>> {
        return shelfAndBookDao.getAllBooksForShelfStream(shelfId).map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun addBookToLibrary(bookFormData: BookFormData): Book.Id {
        val bookId = Book.Id(UUID.randomUUID().toString())

        val bookEntity = bookFormData
            .toBookEntity(
                id = bookId,
                dateAdded = LocalDateTime.now().toString(),
                lastModifiedTimestamp = Clock.System.now().toString(),
            )
            .copy(
                isInLibrary = true,
                isPendingSync = true,
            )

        bookDao.insertBook(bookEntity)
        return bookId
    }

    override suspend fun updateBookInLibrary(bookId: Book.Id, bookFormData: BookFormData) {
        Timber.d("addOrUpdateBook($bookId, $bookFormData)")

        bookDao.updateBook(
            bookId = bookId,
            isPendingSync = true,
            title = bookFormData.title,
            author = bookFormData.author,
            description = bookFormData.description,
            subject = bookFormData.subject,
            binding = bookFormData.binding,
            isbn = bookFormData.isbn,
            publisher = bookFormData.publisher,
            yearPublished = bookFormData.yearPublished,
            thumbnailLink = bookFormData.thumbnailLink,
            userRating = bookFormData.userRating,
            pageCount = bookFormData.pageCount,
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
    }

    override suspend fun toggleBookInShelf(bookId: Book.Id, shelfId: Shelf.Id, isBookInShelf: Boolean) {
        val shelvesWithBooksEntity = ShelfWithBookEntity(
            bookId = bookId,
            shelfId = shelfId,
            isPendingSync = true,
            isDeleted = isBookInShelf.not(),
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
        shelfAndBookDao.insertOrUpdateShelfWithBook(shelvesWithBooksEntity)
    }

    override suspend fun searchMyLibraryBooks(query: String): List<Book> {
        return bookDao.getLibraryBooksForQuery(query).map(BookEntity::asExternalModel)
    }
}
