package dev.zezula.books.data

import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.data.model.shelf.ShelfForBook
import kotlinx.coroutines.flow.Flow

interface ShelvesRepository {

    fun getShelvesStream(): Flow<List<Shelf>>

    fun getShelvesForBookStream(bookId: String): Flow<List<ShelfForBook>>

    suspend fun createShelf(shelfTitle: String)

    suspend fun updateShelf(shelfId: String, updatedTitle: String)

    suspend fun softDeleteShelf(shelf: Shelf)
}
