package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.fromNetworkBook
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.data.model.note.asExternalModel
import dev.zezula.books.data.model.note.fromNetworkNote
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.model.shelf.fromNetworkShelf
import dev.zezula.books.data.model.shelf.fromNetworkShelfWithBook
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class BooksRepositoryImpl(
    private val booksDao: BookDao,
    private val networkDataSource: NetworkDataSource,
    private val shelfAndBookDao: ShelfAndBookDao,
) : BooksRepository {

    override fun getBooksForShelfAsStream(shelfId: String): Flow<List<Book>> {
        return shelfAndBookDao.getBooksForShelfAsStream(shelfId).map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getNotesStream(bookId: String): Flow<List<Note>> {
        return booksDao.getNotesForBook(bookId).map {
            it.map { noteEntity ->
                Note(
                    id = noteEntity.id,
                    bookId = noteEntity.bookId,
                    text = noteEntity.text,
                    dateAdded = noteEntity.dateAdded,
                    page = noteEntity.page,
                    type = noteEntity.type,
                )
            }
        }
    }

    override suspend fun addOrUpdateNote(
        noteId: String?,
        bookId: String,
        noteFormData: NoteFormData,
    ): Note {
        val finalNoteId = noteId ?: UUID.randomUUID().toString()
        val networkNote = NetworkNote(
            id = finalNoteId,
            bookId = bookId,
            text = noteFormData.text,
            dateAdded = noteFormData.dateAdded ?: LocalDateTime.now().toString(),
            page = noteFormData.page,
            type = noteFormData.type,
        )
        networkDataSource.addOrUpdateNote(networkNote)

        val networkNoteEntity = fromNetworkNote(
            networkNote = networkNote,
            bookId = bookId,
        )
        booksDao.addOrUpdateNote(networkNoteEntity)
        return networkNoteEntity.asExternalModel()
    }

    override fun getAllBooksStream(): Flow<List<Book>> {
        return booksDao.getAllBooksAsStream().map {
            it.map(BookEntity::asExternalModel)
        }
    }

    override fun getBookStream(bookId: String): Flow<Book?> {
        return booksDao.getBook(bookId)
            .map {
                it?.asExternalModel()
            }
    }

    override suspend fun searchMyLibraryBooks(query: String): List<Book> {
        return booksDao.getBooksForQuery(query).map(BookEntity::asExternalModel)
    }

    override suspend fun getBook(bookId: String): Book? {
        return getBookStream(bookId).first()
    }

    override suspend fun addOrUpdateBook(bookId: String, bookFormData: BookFormData): Book {
        Timber.d("addOrUpdateBook($bookId, $bookFormData)")
        val networkBook = NetworkBook(
            id = bookId,
            title = bookFormData.title,
            author = bookFormData.author,
            isbn = bookFormData.isbn,
            description = bookFormData.description,
            dateAdded = bookFormData.dateAdded ?: LocalDateTime.now().toString(),
            publisher = bookFormData.publisher,
            thumbnailLink = bookFormData.thumbnailLink,
            yearPublished = bookFormData.yearPublished,
            pageCount = bookFormData.pageCount,
            userRating = bookFormData.userRating,
        )
        networkDataSource.addOrUpdateBook(networkBook)

        val bookEntity = fromNetworkBook(networkBook)
        booksDao.addOrUpdate(bookEntity)
        return bookEntity.asExternalModel()
    }

    override suspend fun addBook(bookFormData: BookFormData): Book {
        val createdId = UUID.randomUUID().toString()
        return addOrUpdateBook(createdId, bookFormData)
    }

    override suspend fun refreshBooks() {
        val numberOfBooks = booksDao.getBookCount()
        // TODO: implement proper syncing. Right now, the firestore is used as a simple online "back up" (which is
        //  downloaded only when there are no books in the app database)
        if (numberOfBooks == 0) {
            networkDataSource.getBooks().forEach { networkBook ->
                val bookEntity = fromNetworkBook(networkBook)
                booksDao.addOrUpdate(bookEntity)

                networkDataSource.getNotes(bookEntity.id).forEach { networkNote ->
                    val networkNoteEntity = fromNetworkNote(
                        networkNote = networkNote,
                        bookId = bookEntity.id,
                    )
                    booksDao.addOrUpdateNote(networkNoteEntity)
                }
            }
            networkDataSource.getShelves().forEach { networkShelf ->
                shelfAndBookDao.addOrUpdate(fromNetworkShelf(networkShelf))
            }
            networkDataSource.getShelvesWithBooks().forEach { networkShelfWithBook ->
                shelfAndBookDao.addBookToShelf(fromNetworkShelfWithBook(networkShelfWithBook))
            }
        }
    }

    override suspend fun deleteBook(bookId: String) {
        networkDataSource.deleteBook(bookId)
        booksDao.delete(bookId)
    }

    override suspend fun deleteNote(noteId: String, bookId: String) {
        networkDataSource.deleteNote(noteId = noteId, bookId = bookId)
        booksDao.deleteNote(noteId)
    }

    override suspend fun updateBookInShelf(bookId: String, shelfId: String, isBookInShelf: Boolean) {
        val shelvesWithBooksEntity = ShelfWithBookEntity(bookId = bookId, shelfId = shelfId)

        networkDataSource.updateBookInShelf(shelfId, bookId, isBookInShelf)

        if (isBookInShelf) {
            shelfAndBookDao.addBookToShelf(shelvesWithBooksEntity)
        } else {
            shelfAndBookDao.removeBookFromShelf(shelvesWithBooksEntity)
        }
    }

    override suspend fun getBookId(isbn: String): String? {
        val dbBooks = booksDao.getForIsbn(isbn)
        return dbBooks.firstOrNull()?.id
    }
}
