package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookSuggestionEntity
import dev.zezula.books.data.model.book.LibraryBookEntity
import dev.zezula.books.data.model.book.SearchBookResultEntity
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeBookDaoImpl : BookDao {

    private var bookFlow: MutableStateFlow<Map<String, BookEntity>> = MutableStateFlow(emptyMap())

    override suspend fun addToLibraryBooks(libraryBookEntity: LibraryBookEntity) {
    }

    override fun getAllLibraryBooksStream(): Flow<List<BookEntity>> {
        return getAllBooksStream()
    }

    override fun getLibraryBookStream(bookId: String): Flow<LibraryBookEntity?> {
        return getAllBooksStream().map { bookList ->
            val book = bookList.firstOrNull { book -> book.id == bookId }
            if (book != null) {
                LibraryBookEntity(book.id)
            } else {
                null
            }
        }
    }

    override fun getAllSearchResultBooksStream(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToSearchBookResults(searchBookResultEntity: SearchBookResultEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllSearchBookResults() {
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

    override suspend fun getLibraryBooksForForQuery(query: String): List<BookEntity> {
        TODO("Searching books is not yet implemented")
    }

    override suspend fun getForIsbn(isbn: String): List<BookEntity> =
        bookFlow.first().values.filter { entity -> entity.isbn == isbn }

    override suspend fun getBookCount(): Int = bookFlow.first().size

    override suspend fun addOrUpdate(book: BookEntity) {
        addOrUpdate(listOf(book))
    }

    override suspend fun addOrUpdate(books: List<BookEntity>) {
        bookFlow.update { bookMap ->
            bookMap.toMutableMap().apply {
                putAll(books.associateBy { entity -> entity.id })
            }
        }
    }

    override suspend fun delete(bookId: String) {
        bookFlow.update { bookMap ->
            bookMap.toMutableMap().apply {
                remove(bookId)
            }
        }
    }

    override suspend fun deleteAll() {
        bookFlow.value = emptyMap()
    }

    private fun MutableStateFlow<Map<String, BookEntity>>.asBooks() = map { it.values.toList() }
}
