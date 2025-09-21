package dev.zezula.books.data.repositories

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.core.model.ShelfForBook
import dev.zezula.books.data.database.ShelfAndBookDao
import dev.zezula.books.data.database.ShelfDao
import dev.zezula.books.data.database.entities.ShelfEntity
import dev.zezula.books.data.database.entities.ShelfForBookEntity
import dev.zezula.books.data.database.entities.ShelfWithBookCountEntity
import dev.zezula.books.data.database.entities.asExternalModel
import dev.zezula.books.domain.repositories.ShelvesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import java.time.LocalDateTime
import java.util.UUID

class ShelvesRepositoryImpl(
    private val shelvesAndBooksDao: ShelfAndBookDao,
    private val shelfDao: ShelfDao,
) : ShelvesRepository {

    override fun getAllShelvesFlow(): Flow<List<Shelf>> {
        return shelvesAndBooksDao.getAllShelvesFlow()
            .map { shelfEntities ->
                shelfEntities.map(ShelfWithBookCountEntity::asExternalModel)
            }
    }

    override fun getAllShelvesForBookFlow(bookId: Book.Id): Flow<List<ShelfForBook>> {
        return shelvesAndBooksDao.getAllShelvesForBookFlow(bookId).map { list ->
            list.map(ShelfForBookEntity::asExternalModel)
        }
    }

    override suspend fun createShelf(shelfTitle: String) {
        val shelfId = Shelf.Id(UUID.randomUUID().toString())

        val shelf = ShelfEntity(
            id = shelfId,
            dateAdded = LocalDateTime.now().toString(),
            title = shelfTitle,
            isPendingSync = true,
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
        shelfDao.insertShelf(shelf)
    }

    override suspend fun updateShelf(shelfId: Shelf.Id, updatedTitle: String) {
        shelfDao.updateShelf(
            shelfId = shelfId,
            title = updatedTitle,
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
    }

    override suspend fun softDeleteShelf(shelf: Shelf) {
        shelfDao.softDeleteShelf(
            shelfId = shelf.id,
            lastModifiedTimestamp = Clock.System.now().toString(),
        )
        shelvesAndBooksDao.softDeleteShelvesWithBooksForShelf(shelfId = shelf.id, lastModifiedTimestamp = Clock.System.now().toString())
    }
}