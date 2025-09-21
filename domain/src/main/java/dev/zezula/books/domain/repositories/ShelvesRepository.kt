package dev.zezula.books.domain.repositories

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.core.model.ShelfForBook
import kotlinx.coroutines.flow.Flow

interface ShelvesRepository {

    fun getAllShelvesFlow(): Flow<List<Shelf>>

    fun getAllShelvesForBookFlow(bookId: Book.Id): Flow<List<ShelfForBook>>

    suspend fun createShelf(shelfTitle: String)

    suspend fun updateShelf(shelfId: Shelf.Id, updatedTitle: String)

    suspend fun softDeleteShelf(shelf: Shelf)
}