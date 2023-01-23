package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.previewBookEntities
import dev.zezula.books.data.model.shelf.*
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.*

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
        // FIXME: add proper book<->shelf relation
        return when (bookId) {
            previewBookEntities[0].id -> {
                flowOf(previewShelfEntities.map { entity ->
                    ShelfForBookEntity(id = entity.id, title = entity.title, true)
                })
            }
            previewBookEntities[1].id -> {
                flowOf(previewShelfEntities.map { entity ->
                    ShelfForBookEntity(id = entity.id, title = entity.title, true)
                })
            }
            else -> flowOf(emptyList())
        }
    }

    override fun getBooksForShelfAsStream(shelfId: String): Flow<List<BookEntity>> {
        return when (shelfId) {
            previewShelfEntities[0].id -> flowOf(listOf(previewBookEntities[0]))
            previewShelfEntities[1].id -> flowOf(listOf(previewBookEntities[1]))
            else -> flowOf(emptyList())
        }
    }
}