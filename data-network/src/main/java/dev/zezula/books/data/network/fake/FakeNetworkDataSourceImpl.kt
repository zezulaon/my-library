package dev.zezula.books.data.network.fake

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.NetworkMigrationData
import dev.zezula.books.core.model.previewBooks
import dev.zezula.books.core.model.previewNotes
import dev.zezula.books.core.model.previewShelves
import dev.zezula.books.data.network.NetworkBook
import dev.zezula.books.data.network.NetworkNote
import dev.zezula.books.data.network.NetworkShelf
import dev.zezula.books.data.network.NetworkShelfWithBook

open class FakeNetworkDataSourceImpl : dev.zezula.books.data.network.api.NetworkDataSource {

    private val booksMap: MutableMap<Book.Id, NetworkBook> = previewBooks
        .map { book ->
            NetworkBook(
                id = book.id.value,
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
        .associateBy { book -> Book.Id(book.id!!) }
        .toMutableMap()

    private val shelvesMap: MutableMap<String, NetworkShelf> = previewShelves
        .map { shelf ->
            NetworkShelf(id = shelf.id.value, dateAdded = shelf.dateAdded, title = shelf.title)
        }
        .associateBy { book -> book.id!! }
        .toMutableMap()

    private val notesMap: MutableMap<String, NetworkNote> = previewNotes
        .map { note ->
            NetworkNote(
                id = note.id.value,
                bookId = note.bookId.value,
                dateAdded = note.dateAdded,
                text = note.text,
            )
        }
        .associateBy { note -> note.id!! }
        .toMutableMap()

    override suspend fun addOrUpdateBook(book: NetworkBook): NetworkBook {
        booksMap[Book.Id(book.id!!)] = book
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
}