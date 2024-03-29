package dev.zezula.books.data

import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.data.model.shelf.ShelfForBookEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookCountEntity
import dev.zezula.books.data.model.shelf.asExternalModel
import dev.zezula.books.data.model.shelf.fromNetworkShelf
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class ShelvesRepositoryImpl(
    private val shelvesAndBooksDao: ShelfAndBookDao,
    private val networkDataSource: NetworkDataSource,
) : ShelvesRepository {

    override fun getShelvesStream(): Flow<List<Shelf>> {
        return shelvesAndBooksDao.getAllShelvesStream().map { shelfEntities ->
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
        addOrUpdateShelf(shelfId = createdId, shelfTitle = shelfTitle)
    }

    override suspend fun updateShelf(shelfId: String, updatedTitle: String) {
        addOrUpdateShelf(shelfId = shelfId, shelfTitle = updatedTitle)
    }

    override suspend fun addOrUpdateShelf(shelfId: String, shelfTitle: String) {
        Timber.d("addOrUpdateShelf($shelfId, $shelfTitle)")
        val networkShelf = NetworkShelf(id = shelfId, dateAdded = LocalDateTime.now().toString(), title = shelfTitle)
        networkDataSource.addOrUpdateShelf(networkShelf)

        val shelf = fromNetworkShelf(networkShelf)
        shelvesAndBooksDao.addOrUpdate(shelf)
    }

    override suspend fun deleteShelf(shelf: Shelf) {
        networkDataSource.deleteShelf(shelf.id)
        shelvesAndBooksDao.delete(shelf.id)
    }
}
