package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.data.model.shelf.ShelfForBookEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookCountEntity
import dev.zezula.books.data.model.shelf.asExternalModel
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.ShelfDao
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
