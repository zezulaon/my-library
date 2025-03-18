package dev.zezula.books.data

import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.data.model.shelf.ShelfForBookEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookCountEntity
import dev.zezula.books.data.model.shelf.asExternalModel
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

class ShelvesRepositoryImpl(
    private val shelvesAndBooksDao: ShelfAndBookDao,
) : ShelvesRepository {

    override fun getAllShelvesFlow(): Flow<List<Shelf>> {
        return shelvesAndBooksDao.getAllShelvesFlow()
            .map { shelfEntities ->
            shelfEntities.map(ShelfWithBookCountEntity::asExternalModel)
        }
    }

    override fun getAllShelvesForBookFlow(bookId: String): Flow<List<ShelfForBook>> {
        return shelvesAndBooksDao.getAllShelvesForBookFlow(bookId).map { list ->
            list.map(ShelfForBookEntity::asExternalModel)
        }
    }

    override suspend fun createShelf(shelfTitle: String) {
        val shelfId = UUID.randomUUID().toString()

        val shelf = ShelfEntity(
            id = shelfId,
            dateAdded = LocalDateTime.now().toString(),
            title = shelfTitle,
            isPendingSync = true,
        )
        shelvesAndBooksDao.insertShelf(shelf)
    }

    override suspend fun updateShelf(shelfId: String, updatedTitle: String) {
        shelvesAndBooksDao.updateShelf(shelfId = shelfId, title = updatedTitle)
    }

    override suspend fun softDeleteShelf(shelf: Shelf) {
        shelvesAndBooksDao.softDeleteShelf(shelf.id)
        shelvesAndBooksDao.softDeleteShelvesWithBooksForShelf(shelf.id)
    }
}
