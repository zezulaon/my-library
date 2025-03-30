package dev.zezula.books.data.source.network.fake

import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.note.previewNotes
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook
import dev.zezula.books.data.model.shelf.previewShelves
import dev.zezula.books.data.model.user.NetworkMigrationData
import dev.zezula.books.data.source.network.NetworkDataSource

open class FakeNetworkDataSourceImpl : NetworkDataSource {

    private val booksMap: MutableMap<String, NetworkBook> = previewBooks
        .map { book ->
            NetworkBook(
                id = book.id,
                dateAdded = book.dateAdded,
                title = book.title,
                author = book.author,
                description = book.description,
                isbn = book.isbn,
                publisher = book.publisher,
                yearPublished = book.yearPublished,
                pageCount = book.pageCount,
                thumbnailLink = book.thumbnailLink,
            )
        }
        .associateBy { book -> book.id!! }
        .toMutableMap()

    private val shelvesMap: MutableMap<String, NetworkShelf> = previewShelves
        .map { shelf ->
            NetworkShelf(id = shelf.id, dateAdded = shelf.dateAdded, title = shelf.title)
        }
        .associateBy { book -> book.id!! }
        .toMutableMap()

    private val notesMap: MutableMap<String, NetworkNote> = previewNotes
        .map { note ->
            NetworkNote(
                id = note.id,
                bookId = note.bookId,
                dateAdded = note.dateAdded,
                text = note.text,
            )
        }
        .associateBy { note -> note.id!! }
        .toMutableMap()

    override suspend fun addOrUpdateBook(book: NetworkBook): NetworkBook {
        booksMap[book.id!!] = book
        return book
    }

    override suspend fun getModifiedShelves(lastModifiedTimestamp: String?): List<NetworkShelf> {
        return emptyList()
    }

    override suspend fun addOrUpdateShelf(shelf: NetworkShelf): NetworkShelf {
        shelvesMap[shelf.id!!] = shelf
        return shelf
    }

    override suspend fun getModifiedBooks(lastModifiedTimestamp: String?): List<NetworkBook> {
        return emptyList()
    }

    override suspend fun getModifiedShelvesWithBooks(lastModifiedTimestamp: String?): List<NetworkShelfWithBook> {
        return emptyList()
    }

    override suspend fun getModifiedNotes(lastModifiedTimestamp: String?): List<NetworkNote> {
        return emptyList()
    }

    override suspend fun deleteShelf(shelfId: String) {
        shelvesMap.remove(shelfId)
    }

    override suspend fun getMigrationData(): NetworkMigrationData {
        return NetworkMigrationData()
    }

    override suspend fun updateMigrationData(networkMigrationData: NetworkMigrationData) {
        // Not used in this fake
    }

    override suspend fun updateBookInShelf(shelfWithBook: NetworkShelfWithBook) {
        // Not used in this fake
    }

    override suspend fun addOrUpdateNote(note: NetworkNote): NetworkNote {
        notesMap[note.id!!] = note
        return note
    }

    override suspend fun deleteNote(noteId: String, bookId: String) {
        notesMap.remove(noteId)
    }
}
