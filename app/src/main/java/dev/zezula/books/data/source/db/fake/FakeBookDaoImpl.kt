package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.*

class FakeBookDaoImpl : BookDao {

    private var bookFlow: MutableStateFlow<Map<String, BookEntity>> = MutableStateFlow(
        previewBooks.map { book ->
            BookEntity(
                id = book.id,
                dateAdded = book.dateAdded,
                title = book.title,
                author = book.author,
                description = book.description,
                isbn = book.isbn,
                publisher = book.publisher,
                yearPublished = book.yearPublished,
                thumbnailLink = book.thumbnailLink,
                pageCount = book.pageCount
            )
        }
            .associateBy { entity -> entity.id }
    )

    override fun getAllBooksAsStream(): Flow<List<BookEntity>> = bookFlow.asBooks()

    override fun getBook(bookId: String): Flow<BookEntity?> = bookFlow.map { it[bookId] }

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