package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.shelf.*
import dev.zezula.books.data.source.db.ShelfDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeShelfDaoImpl : ShelfDao {

    private var shelves: MutableStateFlow<List<ShelfEntity>> = MutableStateFlow(
        previewShelves.map { shelf ->
            ShelfEntity(
                id = shelf.id,
                dateAdded = shelf.dateAdded,
                title = shelf.title,
            )
        }
    )

    override fun getAllAsStream(): Flow<List<ShelfWithBookCountEntity>> {
        return shelves.map {
            it.map { entity ->
                ShelfWithBookCountEntity(
                    id = entity.id,
                    dateAdded = entity.dateAdded,
                    title = entity.title,
                    numberOfBooks = 0
                )
            }
        }
    }

    override suspend fun addOrUpdate(shelf: ShelfEntity) {
        shelves.update { list ->
            list
                .filterNot { entity -> entity.id == shelf.id }
                .toMutableList()
                .apply { add(shelf) }
        }
    }

    override suspend fun delete(shelfId: String) {
        shelves.update { list ->
            list.filterNot { entity -> entity.id == shelfId }
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
}