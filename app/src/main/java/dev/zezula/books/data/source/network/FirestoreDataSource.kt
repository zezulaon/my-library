package dev.zezula.books.data.source.network

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook
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

    override suspend fun deleteNote(noteId: String, bookId: String) {
        Timber.d("deleteNote (noteId=$noteId)")
        booksCollection.document(bookId).collection(COLLECTION_NOTES).document(noteId).update(FIELD_IS_DELETED, true).await()
    }

    override suspend fun addOrUpdateShelf(shelf: NetworkShelf): NetworkShelf {
        Timber.d("addOrUpdateShelf(shelf=$shelf)")

        checkNotNull(shelf.id) { "Shelf needs [id] property" }

        shelvesCollection.document(shelf.id).set(shelf).await()
        return shelf
    }

    override suspend fun deleteShelf(shelfId: String) {
        Timber.d("deleteShelf(shelfId=$shelfId)")

        shelvesCollection.document(shelfId).update(FIELD_IS_DELETED, true).await()
    }

    override suspend fun updateBookInShelf(shelfWithBook: NetworkShelfWithBook) {
        Timber.d("updateBookInToShelf(shelfWithBook=$shelfWithBook)")

        val shelfId = checkNotNull(shelfWithBook.shelfId) { "Shelf ID is null" }
        val bookId = checkNotNull(shelfWithBook.bookId) { "Book ID is null" }

        val shelfWithBookId = createShelfWithBookId(shelfId = shelfId, bookId = bookId)

        shelvesWithBooksCollection.document(shelfWithBookId).set(shelfWithBook).await()
    }

    private fun createShelfWithBookId(shelfId: String, bookId: String) = "${shelfId}_$bookId"
}
