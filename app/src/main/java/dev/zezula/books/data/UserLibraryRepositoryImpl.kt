package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.asBookEntity
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun moveExistingBookToLibrary(bookId: String) {
        bookDao.addToLibraryBooks(bookId = bookId, dateAdded = LocalDateTime.now().toString())
    }

    override fun isBookInLibrary(bookId: String): Flow<Boolean> {
        return bookDao.isBookInLibrary(bookId)
    }

    override fun getAllBooksForShelfStream(shelfId: String): Flow<List<Book>> {
        return shelfAndBookDao.getAllBooksForShelfStream(shelfId).map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override suspend fun addBookToLibrary(bookFormData: BookFormData): String {
        val bookId = UUID.randomUUID().toString()

        val bookEntity = bookFormData
            .asBookEntity(id = bookId)
            .copy(
                isInLibrary = true,
                isPendingSync = true,
            )

        bookDao.insertBook(bookEntity)
        return bookId
    }

    override suspend fun updateBookInLibrary(bookId: String, bookFormData: BookFormData) {
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
            pageCount = bookFormData.pageCount
        )
    }

    override suspend fun updateBookCover(bookId: String, thumbnailLink: String) {
        bookDao.updateBookCover(bookId, thumbnailLink)
    }

    override suspend fun toggleBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean) {
        val shelvesWithBooksEntity = ShelfWithBookEntity(
            bookId = bookId,
            shelfId = shelfId,
            isPendingSync = true,
            isDeleted = isBookInShelf.not()
        )
        shelfAndBookDao.addBookToShelf(shelvesWithBooksEntity)
    }

    override suspend fun refreshBooks() {
        val numberOfBooks = bookDao.getBookCount()
        // FIXME: implement proper syncing. Right now, the firestore is used as a simple online "back up" (which is
        //  downloaded only when there are no books in the app database)
//        if (numberOfBooks == 0) {
//            networkDataSource.getBooks().forEach { networkBook ->
//                val bookEntity = fromNetworkBook(networkBook)
//                bookDao.addOrUpdate(bookEntity)
//
//                // Associate the book with the library
//                bookDao.addToLibraryBooks(LibraryBookEntity(bookId = bookEntity.id))
//
//                networkDataSource.getNotesForBook(bookEntity.id).forEach { networkNote ->
//                    val networkNoteEntity = fromNetworkNote(
//                        networkNote = networkNote,
//                        bookId = bookEntity.id,
//                    )
//                    noteDao.addOrUpdateNote(networkNoteEntity)
//                }
//            }
//            networkDataSource.getShelves().forEach { networkShelf ->
//                shelfAndBookDao.addOrUpdate(fromNetworkShelf(networkShelf))
//            }
//            networkDataSource.getShelvesWithBooks().forEach { networkShelfWithBook ->
//                shelfAndBookDao.addBookToShelf(fromNetworkShelfWithBook(networkShelfWithBook))
//            }
//        }
    }

    override suspend fun searchMyLibraryBooks(query: String): List<Book> {
        return bookDao.getLibraryBooksForQuery(query).map(BookEntity::asExternalModel)
    }
}
