package dev.zezula.books.data.source.network

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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

    private fun getCurrentUserId(): String = auth.currentUser?.uid ?: throw IllegalStateException("Missing firebase user")

    private fun userDocument(): DocumentReference = db.collection(COLLECTION_ID_USERS).document(getCurrentUserId())

    private fun booksCollection(): CollectionReference =
        db.collection(COLLECTION_ID_USERS).document(getCurrentUserId()).collection(COLLECTION_ID_BOOKS)

    private fun shelvesCollection(): CollectionReference =
        db.collection(COLLECTION_ID_USERS).document(getCurrentUserId()).collection(COLLECTION_ID_SHELVES)

    private fun shelvesWithBooksCollection(): CollectionReference =
        db.collection(COLLECTION_ID_USERS).document(getCurrentUserId()).collection(COLLECTION_ID_SHELVES_WITH_BOOKS)

    private fun notesCollection(): CollectionReference =
        db.collection(COLLECTION_ID_USERS).document(getCurrentUserId()).collection(COLLECTION_NOTES)

    override suspend fun getMigrationData(): NetworkMigrationData {
        Timber.d("getMigrationData()")
        return userDocument().get().await()
            .toObject(NetworkMigrationData::class.java).also {
                Timber.d("Deserialized migration data to: $it")
            } ?: NetworkMigrationData()
    }

    override suspend fun updateMigrationData(networkMigrationData: NetworkMigrationData) {
        Timber.d("updateMigrationData(networkMigrationData=$networkMigrationData)")
        val userDocumentExists = userDocument().get().await().exists()
        if (userDocumentExists) {
            userDocument().update(networkMigrationData.toMapValues()).await()
        } else {
            userDocument().set(networkMigrationData).await()
        }
    }

    override suspend fun getModifiedShelves(lastModifiedTimestamp: String?): List<NetworkShelf> {
        Timber.d("getModifiedShelves(lastModifiedTimestamp=$lastModifiedTimestamp)")

        return if (lastModifiedTimestamp == null) {
            shelvesCollection()
        } else {
            shelvesCollection().whereGreaterThan(FIELD_LAST_MODIFIED_TIMESTAMP, lastModifiedTimestamp)
        }.get().await()
            .also { Timber.d("Found ${it.size()} modified shelves") }
            .map {
                it.toObject(NetworkShelf::class.java)
            }
            .onEach {
                Timber.d("Deserialized shelf: $it")
            }
    }

    override suspend fun getModifiedShelvesWithBooks(lastModifiedTimestamp: String?): List<NetworkShelfWithBook> {
        Timber.d("getModifiedShelvesWithBooks(lastModifiedTimestamp=$lastModifiedTimestamp)")

        return if (lastModifiedTimestamp == null) {
            shelvesWithBooksCollection()
        } else {
            shelvesWithBooksCollection().whereGreaterThan(FIELD_LAST_MODIFIED_TIMESTAMP, lastModifiedTimestamp)
        }.get().await()
            .also { Timber.d("Found ${it.size()} modified shelves with books") }
            .map {
                it.toObject(NetworkShelfWithBook::class.java)
            }
            .onEach {
                Timber.d("Deserialized shelfWithBook: $it")
            }
    }

    override suspend fun getModifiedBooks(lastModifiedTimestamp: String?): List<NetworkBook> {
        Timber.d("getModifiedBooks(lastModifiedTimestamp=$lastModifiedTimestamp)")

        return if (lastModifiedTimestamp == null) {
            booksCollection()
        } else {
            booksCollection().whereGreaterThan(FIELD_LAST_MODIFIED_TIMESTAMP, lastModifiedTimestamp)
        }.get().await()
            .also { Timber.d("Found ${it.size()} modified books") }
            .map {
                it.toObject(NetworkBook::class.java)
            }
            .onEach {
                Timber.d("Deserialized book: $it")
            }
    }

    override suspend fun getModifiedNotes(lastModifiedTimestamp: String?): List<NetworkNote> {
        Timber.d("getModifiedNotes(lastModifiedTimestamp=$lastModifiedTimestamp)")

        return if (lastModifiedTimestamp == null) {
            notesCollection()
        } else {
            notesCollection().whereGreaterThan(FIELD_LAST_MODIFIED_TIMESTAMP, lastModifiedTimestamp)
        }.get().await()
            .also { Timber.d("Found ${it.size()} modified notes") }
            .map {
                it.toObject(NetworkNote::class.java)
            }
            .onEach {
                Timber.d("Deserialized note: $it")
            }
    }

    override suspend fun addOrUpdateBook(book: NetworkBook): NetworkBook {
        Timber.d("addOrUpdate(book=$book)")
        checkNotNull(book.id) { "Book needs [id] property" }

        booksCollection().document(book.id).set(book).await()
        return book
    }

    override suspend fun addOrUpdateNote(note: NetworkNote): NetworkNote {
        Timber.d("addOrUpdate(note=$note)")
        checkNotNull(note.id) { "Note needs [id] property" }
        checkNotNull(note.bookId) { "Note needs [bookId] property" }

        val bookWithNoteId = createBookWithNoteId(bookId = note.bookId, noteId = note.id)
        notesCollection().document(bookWithNoteId).set(note).await()
        return note
    }

    override suspend fun addOrUpdateShelf(shelf: NetworkShelf): NetworkShelf {
        Timber.d("addOrUpdateShelf(shelf=$shelf)")

        checkNotNull(shelf.id) { "Shelf needs [id] property" }

        shelvesCollection().document(shelf.id).set(shelf).await()
        return shelf
    }

    override suspend fun updateBookInShelf(shelfWithBook: NetworkShelfWithBook) {
        Timber.d("updateBookInShelf(shelfWithBook=$shelfWithBook)")

        val shelfId = checkNotNull(shelfWithBook.shelfId) { "Shelf ID is null" }
        val bookId = checkNotNull(shelfWithBook.bookId) { "Book ID is null" }

        val shelfWithBookId = createShelfWithBookId(shelfId = shelfId, bookId = bookId)

        shelvesWithBooksCollection().document(shelfWithBookId).set(shelfWithBook).await()
    }

    private fun createShelfWithBookId(shelfId: String, bookId: String) = "${shelfId}_$bookId"

    private fun createBookWithNoteId(bookId: String, noteId: String) = "${bookId}_$noteId"
}
