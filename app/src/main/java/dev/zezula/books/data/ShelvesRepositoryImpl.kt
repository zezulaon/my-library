package dev.zezula.books.data

import dev.zezula.books.data.model.shelf.*
import dev.zezula.books.data.source.db.ShelfDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.*

class ShelvesRepositoryImpl(
    private val shelvesDbDataSource: ShelfDao,
    private val networkDataSource: NetworkDataSource,
) : ShelvesRepository {

    override fun getShelvesAsStream(): Flow<List<Shelf>> {
        return shelvesDbDataSource.getAllAsStream().map { shelfEntities ->
            shelfEntities.map(ShelfWithBookCountEntity::asExternalModel)
        }
    }

    override fun getShelvesForBookAsStream(bookId: String): Flow<List<ShelfForBook>> {
        return shelvesDbDataSource.getShelvesForBookAsStream(bookId).map { list ->
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
        shelvesDbDataSource.addOrUpdate(shelf)
    }

    override suspend fun deleteShelf(shelf: Shelf) {
        networkDataSource.deleteShelf(shelf.id)
        shelvesDbDataSource.delete(shelf.id)
    }
}