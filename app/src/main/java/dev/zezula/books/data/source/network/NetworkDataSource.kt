package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook

interface NetworkDataSource {

    suspend fun getBooks(): List<NetworkBook>

    suspend fun addOrUpdateBook(book: NetworkBook): NetworkBook

    suspend fun deleteBook(bookId: String)

    suspend fun addOrUpdateShelf(shelf: NetworkShelf): NetworkShelf

    suspend fun deleteShelf(shelfId: String)

    suspend fun getShelves(): List<NetworkShelf>

    suspend fun getShelvesWithBooks(): List<NetworkShelfWithBook>

    suspend fun updateBookInShelf(shelfId: String, bookId: String, isBookInShelf: Boolean)
}
