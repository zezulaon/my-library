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
        return getAllBooksStream()
    }

    override fun isBookInLibrary(bookId: String): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getBooksByIsbn(isbn: String): List<BookEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllSearchedBooksNotInLibrary() {
        TODO("Not yet implemented")
    }

    override suspend fun addToLibraryBooks(bookId: String, dateAdded: String) {
        TODO("Not yet implemented")
    }

    override suspend fun softDeleteBook(bookId: String) {
        TODO("Not yet implemented")
    }

    override fun getAllPendingSyncBooksFlow(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun setPendingSyncStatus(bookId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun resetBookPendingSyncStatus(bookId: String) {
        TODO("Not yet implemented")
    }

    override fun getAllSearchResultBooksStream(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToSearchBookResults(searchBookResultEntity: SearchBookResultEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllSearchBookResultReferences() {
        TODO("Not yet implemented")
    }

    override suspend fun updateBookCover(bookId: String, thumbnailLink: String) {
        TODO("Not yet implemented")
    }

    override fun getSuggestionsForBook(bookId: String): Flow<List<BookEntity>> {
        return getAllBooksStream()
    }

    override suspend fun addToBookSuggestions(bookSuggestionEntity: BookSuggestionEntity) {
        TODO("Not yet implemented")
    }

    override fun getAllBooksStream(): Flow<List<BookEntity>> = bookFlow.asBooks()

    override fun getBookStream(bookId: String): Flow<BookEntity?> = bookFlow.map { it[bookId] }

    override suspend fun getLibraryBooksForQuery(query: String): List<BookEntity> {
        TODO("Searching books is not yet implemented")
    }

    override suspend fun getBookCount(): Int = bookFlow.first().size

    override suspend fun addOrUpdate(book: BookEntity) {
        TODO()
    }

    override suspend fun deleteAll() {
        bookFlow.value = emptyMap()
    }

    private fun MutableStateFlow<Map<String, BookEntity>>.asBooks() = map { it.values.toList() }
}
