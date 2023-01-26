package dev.zezula.books.data.source.network

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook
import dev.zezula.books.data.model.shelf.bookIdProperty
import dev.zezula.books.data.model.shelf.shelfIdProperty
import kotlinx.coroutines.tasks.await
import timber.log.Timber

private const val collectionIdUsers = "users"
private const val collectionIdBooks = "books"
private const val collectionIdShelves = "shelves"
private const val collectionIdShelvesWithBooks = "shelvesWithBooks"

class FirestoreDataSource : NetworkDataSource {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val userId: String by lazy { auth.currentUser?.uid ?: throw IllegalStateException("Missing firebase user") }

    private val booksCollection =
        db.collection(collectionIdUsers).document(userId).collection(collectionIdBooks)

    private val shelvesCollection =
        db.collection(collectionIdUsers).document(userId).collection(collectionIdShelves)

    private val shelvesWithBooksCollection =
        db.collection(collectionIdUsers).document(userId).collection(collectionIdShelvesWithBooks)

    override suspend fun getBooks(): List<NetworkBook> {
        Timber.d("getBooks()")

        return booksCollection.get().await()
            .map {
                it.toObject(NetworkBook::class.java)
            }
            .onEach {
                Timber.d("Deserialized book: $it")
            }
    }

    override suspend fun getShelves(): List<NetworkShelf> {
        Timber.d("getShelves()")

        return shelvesCollection.get().await()
            .map {
                it.toObject(NetworkShelf::class.java)
            }
            .onEach {
                Timber.d("Deserialized shelf: $it")
            }
    }

    override suspend fun getShelvesWithBooks(): List<NetworkShelfWithBook> {
        Timber.d("getShelvesWithBooks()")

        return shelvesWithBooksCollection.get().await()
            .map {
                it.toObject(NetworkShelfWithBook::class.java)
            }
            .onEach {
                Timber.d("Deserialized shelfWithBook: $it")
            }
    }

    override suspend fun addOrUpdateBook(book: NetworkBook): NetworkBook {
        Timber.d("addOrUpdate(book=$book)")
        checkNotNull(book.id) { "Book needs [id] property" }

        booksCollection.document(book.id).set(book).await()
        return book
    }

    override suspend fun deleteBook(bookId: String) {
        Timber.d("deleteBook(bookId=$bookId)")

        // Delete associated book<->shelf connection
        val shelvesBookJoin = shelvesWithBooksCollection.whereEqualTo(bookIdProperty, bookId).get().await()
        val idsToDelete = shelvesBookJoin.map { it.id }
        idsToDelete.forEach { id ->
            shelvesWithBooksCollection.document(id).delete()
        }

        booksCollection.document(bookId).delete().await()
    }

    override suspend fun addOrUpdateShelf(shelf: NetworkShelf): NetworkShelf {
        Timber.d("addOrUpdateShelf(shelf=$shelf)")

        checkNotNull(shelf.id) { "Shelf needs [id] property" }

        shelvesCollection.document(shelf.id).set(shelf).await()
        return shelf
    }

    override suspend fun deleteShelf(shelfId: String) {
        Timber.d("deleteShelf(shelfId=$shelfId)")

        // Delete associated book<->shelf connection
        val shelvesBooksJoin = shelvesWithBooksCollection.whereEqualTo(shelfIdProperty, shelfId).get().await()
        val idsToDelete = shelvesBooksJoin.map { it.id }
        idsToDelete.forEach { id ->
            shelvesWithBooksCollection.document(id).delete()
        }

        shelvesCollection.document(shelfId).delete().await()
    }

    override suspend fun updateBookInShelf(shelfId: String, bookId: String, isBookInShelf: Boolean) {
        Timber.d("updateBookInToShelf(shelfId=$shelfId, bookId=$bookId, isBookInShelf=$isBookInShelf)")

        val shelfWithBookId = createShelfWithBookId(shelfId, bookId)

        if (isBookInShelf) {
            val shelfWithBook = NetworkShelfWithBook(bookId = bookId, shelfId = shelfId)
            shelvesWithBooksCollection.document(shelfWithBookId).set(shelfWithBook).await()
        } else {
            shelvesWithBooksCollection.document(shelfWithBookId).delete().await()
        }
    }

    private fun createShelfWithBookId(shelfId: String, bookId: String) = "${shelfId}_$bookId"
}
