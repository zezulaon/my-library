package dev.zezula.books.data.source.network.fake

import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook
import dev.zezula.books.data.model.shelf.previewShelves
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

    override suspend fun getBooks(): List<NetworkBook> {
        return booksMap.values.toList()
    }

    override suspend fun addOrUpdateBook(book: NetworkBook): NetworkBook {
        booksMap[book.id!!] = book
        return book
    }

    override suspend fun deleteBook(bookId: String) {
        booksMap.remove(bookId)
    }

    override suspend fun addOrUpdateShelf(shelf: NetworkShelf): NetworkShelf {
        shelvesMap[shelf.id!!] = shelf
        return shelf
    }

    override suspend fun deleteShelf(shelfId: String) {
        shelvesMap.remove(shelfId)
    }

    override suspend fun getShelves(): List<NetworkShelf> {
        return shelvesMap.values.toList()
    }

    override suspend fun getShelvesWithBooks(): List<NetworkShelfWithBook> {
        // Starts without any book<->shelf connection
        return emptyList()
    }

    override suspend fun updateBookInShelf(shelfId: String, bookId: String, isBookInShelf: Boolean) {
        // book<->shelf connection not used in this fake
    }

    override suspend fun addOrUpdateNote(note: NetworkNote): NetworkNote {
        TODO("Note updates not yet implemented")
    }

    override suspend fun deleteNote(noteId: String, bookId: String) {
        TODO("Note deletion not yet implemented")
    }
}
