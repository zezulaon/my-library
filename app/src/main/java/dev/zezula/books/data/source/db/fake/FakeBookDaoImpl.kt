package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookSuggestionEntity
import dev.zezula.books.data.model.book.SearchBookResultEntity
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FakeBookDaoImpl : BookDao {

    private var bookFlow: MutableStateFlow<Map<String, BookEntity>> = MutableStateFlow(emptyMap())

    override fun getAllLibraryBooksStream(): Flow<List<BookEntity>> {
        TODO()
    }

    override suspend fun insertBook(book: BookEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun addToLibraryBooks(bookId: String, dateAdded: String, lastModifiedTimestamp: String) {
        TODO("Not yet implemented")
    }

    override suspend fun softDeleteBook(bookId: String, lastModifiedTimestamp: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateBookCover(bookId: String, thumbnailLink: String, lastModifiedTimestamp: String) {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrUpdateBooks(bookEntities: List<BookEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateBook(
        bookId: String,
        isPendingSync: Boolean,
        title: String?,
        author: String?,
        description: String?,
        subject: String?,
        binding: String?,
        isbn: String?,
        publisher: String?,
        yearPublished: Int?,
        thumbnailLink: String?,
        userRating: Int?,
        pageCount: Int?,
        lastModifiedTimestamp: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestLastModifiedTimestamp(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrUpdateBook(book: BookEntity) {
        TODO("Not yet implemented")
    }

    override fun isBookInLibrary(bookId: String): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getBooksByIsbn(isbn: String): List<BookEntity> {
        TODO("Not yet implemented")
    }

    override fun getAllPendingSyncBooksFlow(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun resetBookPendingSyncStatus(bookId: String) {
        TODO("Not yet implemented")
    }

    override fun getBookFlow(bookId: String): Flow<BookEntity?> = bookFlow.map { it[bookId] }

    override suspend fun getLibraryBooksForQuery(query: String): List<BookEntity> {
        TODO("Searching books is not yet implemented")
    }

    override suspend fun getBookCount(): Int = bookFlow.first().size

    private fun MutableStateFlow<Map<String, BookEntity>>.asBooks() = map { it.values.toList() }
}
