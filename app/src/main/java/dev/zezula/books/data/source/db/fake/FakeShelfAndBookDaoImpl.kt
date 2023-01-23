package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.shelf.*
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.*

class FakeShelfAndBookDaoImpl : ShelfAndBookDao {

    private var shelvesFlow: MutableStateFlow<Map<String, ShelfEntity>> = MutableStateFlow(emptyMap())
    private var booksForShelfFlow: MutableStateFlow<Map<String, List<BookEntity>>> = MutableStateFlow(emptyMap())
    private var shelvesForBook: MutableStateFlow<Map<String, List<ShelfForBookEntity>>> = MutableStateFlow(emptyMap())

    override fun getAllShelvesAsStream(): Flow<List<ShelfWithBookCountEntity>> {
        return shelvesFlow.map { shelfMap ->
            shelfMap.map { (key, value) ->
                ShelfWithBookCountEntity(
                    id = key,
                    dateAdded = value.dateAdded,
                    title = value.title,
                    numberOfBooks = 0
                )
            }
        }
    }

    override suspend fun addOrUpdate(shelves: List<ShelfEntity>) {
        shelvesFlow.update { shelfMap ->
            shelfMap.toMutableMap().apply {
                putAll(shelves.associateBy { entity -> entity.id })
            }
        }
    }

    override suspend fun addOrUpdate(shelf: ShelfEntity) {
        addOrUpdate(listOf(shelf))
    }

    override suspend fun delete(shelfId: String) {
        shelvesFlow.update { shelfMap ->
            shelfMap.toMutableMap().apply {
                remove(shelfId)
            }
        }
    }

    override suspend fun addBookToShelf(shelvesWithBooksEntity: ShelfWithBookEntity) {
        throw NotImplementedError("Unused in tests")
    }

    override suspend fun removeBookFromShelf(shelvesWithBooksEntity: ShelfWithBookEntity) {
        throw NotImplementedError("Unused in tests")
    }

    override fun getShelvesForBookAsStream(bookId: String): Flow<List<ShelfForBookEntity>> =
        shelvesForBook.map { it.getOrDefault(bookId, emptyList()) }

    override fun getBooksForShelfAsStream(shelfId: String): Flow<List<BookEntity>> =
        booksForShelfFlow.map { it.getOrDefault(shelfId, emptyList()) }
}