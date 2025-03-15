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
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class ShelvesRepositoryImpl(
    private val shelvesAndBooksDao: ShelfAndBookDao,
) : ShelvesRepository {

    override fun getShelvesStream(): Flow<List<Shelf>> {
        return shelvesAndBooksDao.getAllShelvesStream()
            .map { shelfEntities ->
            shelfEntities.map(ShelfWithBookCountEntity::asExternalModel)
        }
    }

    override fun getShelvesForBookStream(bookId: String): Flow<List<ShelfForBook>> {
        return shelvesAndBooksDao.getShelvesForBookStream(bookId).map { list ->
            list.map(ShelfForBookEntity::asExternalModel)
        }
    }

    override suspend fun createShelf(shelfTitle: String) {
        val createdId = UUID.randomUUID().toString()
        addOrUpdateShelf(shelfId = createdId, shelfTitle = shelfTitle, dateAdded = LocalDateTime.now().toString())
    }

    override suspend fun updateShelf(shelfId: String, updatedTitle: String) {
        // FIXME: date added cannot be created here, the record already exists. This should be refactored into create/update functions (not @Upsert)
        addOrUpdateShelf(shelfId = shelfId, shelfTitle = updatedTitle, dateAdded = LocalDateTime.now().toString())
    }

    private suspend fun addOrUpdateShelf(shelfId: String, shelfTitle: String, dateAdded: String) {
        Timber.d("addOrUpdateShelf($shelfId, $shelfTitle)")

        val shelf = ShelfEntity(
            id = shelfId,
            dateAdded = dateAdded,
            title = shelfTitle,
            // FIXME: move setting this flag to DAO
            isPendingSync = true,
        )
        shelvesAndBooksDao.addOrUpdate(shelf)
    }

    override suspend fun softDeleteShelf(shelf: Shelf) {
        shelvesAndBooksDao.softDeleteShelf(shelf.id)
        shelvesAndBooksDao.softDeleteShelvesWithBooksForShelf(shelf.id)
    }
}
