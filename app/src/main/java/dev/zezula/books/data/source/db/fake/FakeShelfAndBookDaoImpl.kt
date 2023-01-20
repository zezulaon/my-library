package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.shelf.*
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeShelfAndBookDaoImpl : ShelfAndBookDao {

    private var shelvesFlow: MutableStateFlow<Map<String, ShelfEntity>> = MutableStateFlow(
        previewShelves.map { shelf ->
            ShelfEntity(
                id = shelf.id,
                dateAdded = shelf.dateAdded,
                title = shelf.title,
            )
        }
            .associateBy { entity -> entity.id }
    )

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

    override fun getShelvesForBookAsStream(bookId: String): Flow<List<ShelfForBookEntity>> {
        throw NotImplementedError("Unused in tests")
    }

    override fun getBooksForShelfAsStream(shelfId: String): Flow<List<BookEntity>> {
        throw NotImplementedError("Unused in tests")
    }
}