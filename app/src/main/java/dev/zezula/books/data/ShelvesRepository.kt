package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.data.model.shelf.ShelfForBook
import kotlinx.coroutines.flow.Flow

interface ShelvesRepository {

    fun getAllShelvesFlow(): Flow<List<Shelf>>

    fun getAllShelvesForBookFlow(bookId: Book.Id): Flow<List<ShelfForBook>>

    suspend fun createShelf(shelfTitle: String)

    suspend fun updateShelf(shelfId: Shelf.Id, updatedTitle: String)

    suspend fun softDeleteShelf(shelf: Shelf)
}
