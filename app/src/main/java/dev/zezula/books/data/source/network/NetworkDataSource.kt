package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook
import dev.zezula.books.data.model.user.NetworkMigrationData

interface NetworkDataSource {

    suspend fun getMigrationData(): NetworkMigrationData

    suspend fun updateMigrationData(networkMigrationData: NetworkMigrationData)

    suspend fun addOrUpdateBook(book: NetworkBook): NetworkBook

    suspend fun addOrUpdateNote(note: NetworkNote): NetworkNote

    suspend fun deleteNote(noteId: String, bookId: String)

    suspend fun addOrUpdateShelf(shelf: NetworkShelf): NetworkShelf

    suspend fun deleteShelf(shelfId: String)

    suspend fun updateBookInShelf(shelfWithBook: NetworkShelfWithBook)

    suspend fun getModifiedShelves(lastModifiedTimestamp: String?): List<NetworkShelf>

    suspend fun getModifiedBooks(lastModifiedTimestamp: String?): List<NetworkBook>

    suspend fun getModifiedShelvesWithBooks(lastModifiedTimestamp: String?): List<NetworkShelfWithBook>

    suspend fun getModifiedNotes(lastModifiedTimestamp: String?): List<NetworkNote>
}
