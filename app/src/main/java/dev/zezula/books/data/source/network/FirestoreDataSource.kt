package dev.zezula.books.data.source.network

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook
import dev.zezula.books.data.model.shelf.bookIdProperty
import dev.zezula.books.data.model.shelf.shelfIdProperty
import dev.zezula.books.data.model.user.NetworkMigrationData
import dev.zezula.books.data.model.user.toMapValues
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirestoreDataSource : NetworkDataSource {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val userId: String by lazy { auth.currentUser?.uid ?: throw IllegalStateException("Missing firebase user") }

    private val userDocument = db.collection(COLLECTION_ID_USERS).document(userId)

    private val booksCollection =
        db.collection(COLLECTION_ID_USERS).document(userId).collection(COLLECTION_ID_BOOKS)

    private val shelvesCollection =
        db.collection(COLLECTION_ID_USERS).document(userId).collection(COLLECTION_ID_SHELVES)

    private val shelvesWithBooksCollection =
        db.collection(COLLECTION_ID_USERS).document(userId).collection(COLLECTION_ID_SHELVES_WITH_BOOKS)

    override suspend fun getMigrationData(): NetworkMigrationData {
        Timber.d("getMigrationData()")
        return userDocument.get().await()
            .toObject(NetworkMigrationData::class.java).also {
                Timber.d("Deserialized migration data to: $it")
            } ?: NetworkMigrationData()
    }

    override suspend fun updateMigrationData(networkMigrationData: NetworkMigrationData) {
        Timber.d("updateMigrationData(networkMigrationData=$networkMigrationData)")
        val userDocumentExists = userDocument.get().await().exists()
        if (userDocumentExists) {
            userDocument.update(networkMigrationData.toMapValues()).await()
        } else {
            userDocument.set(networkMigrationData).await()
        }
    }

    override suspend fun updateBookCover(bookId: String, thumbnailLink: String) {
        Timber.d("updateBookCover(bookId=$bookId, thumbnailLink=$thumbnailLink)")
        booksCollection.document(bookId).update("thumbnailLink", thumbnailLink).await()
    }

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

    override suspend fun getNotesForBook(bookId: String): List<NetworkNote> {
        return booksCollection.document(bookId).collection(COLLECTION_NOTES).get().await()
            .map {
                it.toObject(NetworkNote::class.java)
            }
            .onEach {
                Timber.d("Deserialized note: $it")
            }
    }

    override suspend fun addOrUpdateNote(note: NetworkNote): NetworkNote {
        Timber.d("addOrUpdate(note=$note)")
        checkNotNull(note.id) { "Note needs [id] property" }
        checkNotNull(note.bookId) { "Note needs [bookId] property" }

        booksCollection.document(note.bookId).collection(COLLECTION_NOTES).document(note.id).set(note).await()
        return note
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

    override suspend fun deleteNote(noteId: String, bookId: String) {
        Timber.d("deleteNote (noteId=$noteId)")
        booksCollection.document(bookId).collection(COLLECTION_NOTES).document(noteId).delete().await()
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
