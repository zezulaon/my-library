package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfForBookEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookCountEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeShelfAndBookDaoImpl : ShelfAndBookDao {

    private var shelvesFlow: MutableStateFlow<Map<String, ShelfEntity>> = MutableStateFlow(emptyMap())
    private var booksForShelfFlow: MutableStateFlow<Map<String, List<BookEntity>>> = MutableStateFlow(emptyMap())
    private var shelvesForBook: MutableStateFlow<Map<String, List<ShelfForBookEntity>>> = MutableStateFlow(emptyMap())

    override fun getAllShelvesStream(): Flow<List<ShelfWithBookCountEntity>> {
        return shelvesFlow.map { shelfMap ->
            shelfMap.map { (key, value) ->
                ShelfWithBookCountEntity(
                    id = key,
                    dateAdded = value.dateAdded,
                    title = value.title,
                    numberOfBooks = 0,
                )
            }
        }
    }

    override suspend fun softDeleteShelvesWithBooksForBook(bookId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun softDeleteShelvesWithBooksForShelf(shelfId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addOrUpdate(shelves: List<ShelfEntity>) {
        shelvesFlow.update { shelfMap ->
            shelfMap.toMutableMap().apply {
                putAll(shelves.associateBy { entity -> entity.id })
            }
        }
    }

    override fun getAllShelvesWithBooksPendingSyncFlow(): Flow<List<ShelfWithBookEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun resetShelvesWithBooksPendingSyncStatus(shelfId: String, bookId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun insertShelf(shelf: ShelfEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateShelf(shelfId: String, title: String) {
        TODO("Not yet implemented")
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

    override fun getShelvesForBookStream(bookId: String): Flow<List<ShelfForBookEntity>> =
        shelvesForBook.map { it.getOrDefault(bookId, emptyList()) }

    override fun getBooksForShelfStream(shelfId: String): Flow<List<BookEntity>> =
        booksForShelfFlow.map { it.getOrDefault(shelfId, emptyList()) }

    override suspend fun softDeleteShelf(shelfId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setPendingSyncStatus(shelfId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun resetPendingSyncStatus(shelfId: String) {
        TODO("Not yet implemented")
    }

    override fun getAllPendingSyncShelvesStream(): Flow<List<ShelfEntity>> {
        TODO("Not yet implemented")
    }
}
