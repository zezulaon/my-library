package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.*

class FakeBookDaoImpl : BookDao {

    private var books: MutableStateFlow<List<BookEntity>> = MutableStateFlow(
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
    )

    override fun getAllBooksAsStream(): Flow<List<BookEntity>> {
        return books
    }

    override fun getForShelfAsStream(shelfId: String): Flow<List<BookEntity>> {
        throw NotImplementedError("Unused in tests")
    }

    override fun getBook(bookId: String): Flow<BookEntity?> {
        return books.map { list ->
            list.firstOrNull { entity -> entity.id == bookId }
        }
    }

    override suspend fun getForIsbn(isbn: String): List<BookEntity> {
        return books.first().filter { entity -> entity.isbn == isbn }
    }

    override suspend fun getBookCount(): Int {
        return books.first().size
    }

    override suspend fun addOrUpdate(book: BookEntity) {
        books.update { list ->
            list.filterNot { oldBook -> oldBook.id == book.id }
                .toMutableList()
                .apply { add(book) }
        }
    }

    override suspend fun delete(bookId: String) {
        books.update { list ->
            list.filterNot { entity -> entity.id == bookId }
        }
    }

    override suspend fun deleteAll() {
        books.value = emptyList()
    }
}