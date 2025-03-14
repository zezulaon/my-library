package dev.zezula.books.domain.sync

import com.google.firebase.firestore.FirebaseFirestoreException
import dev.zezula.books.data.NotesRepository
import dev.zezula.books.data.ShelvesRepository
import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.asNetworkBook
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

// FIXME: review the syncing
class SyncService(
    private val userLibraryRepository: UserLibraryRepository,
    private val shelvesRepository: ShelvesRepository,
    private val networkDataSource: NetworkDataSource,
    private val notesRepository: NotesRepository,
) {

    private val syncStarted: AtomicBoolean = AtomicBoolean(false)

    // FIXME: The sync service should be a singleton whose job can be launched only once. It should handle exceptions/error during sync (it should
    //  not crash the app or stop the sync service).
    private val job = SupervisorJob()

    //    private val coroutineScope = CoroutineScope(job)
    private val coroutineContext = Dispatchers.IO + job
    private val coroutineScope = CoroutineScope(coroutineContext)
//    val x = userLibraryRepository.getAllLibraryBooksStream().col

    @OptIn(FlowPreview::class)
    fun startSync() {
        if (syncStarted.getAndSet(true)) {
            println("### SYNC IS ALREADY IN PROGRESS - skipping")
            return
        }
        println("### startSync()")
        startBooksSync()
        startShelvesSync()
        startShelvesWithBookSync()
        startNotesSync()
    }

    @OptIn(FlowPreview::class)
    private fun startBooksSync() {
        coroutineScope.launch {
            userLibraryRepository.getAllLibraryPendingSyncBooksStream()
                .debounce(5.seconds)
                .onStart {
                    println("### ON SYNC START")
                }
                .onCompletion {
                    println("### ON SYNC COMPLETION")
                }
                .collect { list ->
                    println("### PENDING BOOKS: ${list.map { it.title }}")
                    list.forEach { book ->
                        val isDeleted = userLibraryRepository.isBookDeleted(book.id).first()
                        println("### SYNCING BOOK: ${book.title}")
                        try {
                            if (isDeleted) {
                                println("### DELETING BOOK: ${book.title}")
                                networkDataSource.deleteBook(book.id)
                                userLibraryRepository.resetPendingSyncStatus(book.id)
                            } else {
                                val bookFormData = BookFormData(
                                    title = book.title,
                                    author = book.author,
                                    description = book.description,
                                    isbn = book.isbn,
                                    publisher = book.publisher,
                                    yearPublished = book.yearPublished,
                                    pageCount = book.pageCount,
                                    thumbnailLink = book.thumbnailLink,
                                    userRating = book.userRating,
                                    dateAdded = book.dateAdded,
                                )
                                networkDataSource.addOrUpdateBook(bookFormData.asNetworkBook(book.id))
                                userLibraryRepository.resetPendingSyncStatus(book.id)
                            }
                        } catch (e: FirebaseFirestoreException) { // FIXME: how to handle exception in stream?
                            println("### ERROR: ${e.message}")
                        }

                        //                        userLibraryRepository.syncBook(it)
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    private fun startShelvesSync() {
        coroutineScope.launch {
            shelvesRepository.getAllPendingSyncShelvesStream()
                .debounce(5.seconds)
                .onStart {
                    println("### ON SHELVES SYNC START")
                }
                .onCompletion {
                    println("### ON SHELVES SYNC COMPLETION")
                }
                .collect { list ->
                    println("### PENDING SHELVES: ${list.map { it.title }}")
                    list.forEach { shelf ->
                        val isDeleted = shelf.isDeleted
                        println("### SYNCING SHELF: ${shelf.title}")
                        try {
                            if (isDeleted) {
                                println("### DELETING SHELF: ${shelf.title}")
                                networkDataSource.deleteShelf(shelf.id)
                            } else {
                                networkDataSource.addOrUpdateShelf(
                                    NetworkShelf(
                                        id = shelf.id,
                                        dateAdded = shelf.dateAdded,
                                        title = shelf.title,
                                    )
                                )
                            }
                            shelvesRepository.resetPendingSyncStatus(shelf.id)
                        } catch (e: FirebaseFirestoreException) { // FIXME: how to handle exception in stream?
                            println("### ERROR: ${e.message}")
                        }

                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    private fun startNotesSync() {
        coroutineScope.launch {

            notesRepository.getAllPendingSyncStream()
                .debounce(5.seconds)
                .onStart {
                    println("### ON NOTES SYNC START")
                }
                .onCompletion {
                    println("### ON NOTES SYNC COMPLETION")
                }
                .collect { list ->


                    println("### PENDING NOTES: ${list.map { it.text }}")
                    list.forEach { note ->
                        val isDeleted = note.isDeleted
                        try {
                            if (isDeleted) {
                                // FIXME: can throw NOT_FOUND: No document to update: projects/mylibrary-v3/databases/(default)/documents/...
                                //  log this or repair (force insert entry)?
                                println("### DELETING NOTE: ${note.text}")
                                networkDataSource.deleteNote(note.id, note.bookId)
                            } else {
                                networkDataSource.addOrUpdateNote(
                                    NetworkNote(
                                        id = note.id,
                                        bookId = note.bookId,
                                        text = note.text,
                                        dateAdded = note.dateAdded,
                                        page = note.page,
                                        type = note.type,
                                    )
                                )
                            }
                            notesRepository.resetPendingSyncStatus(note.id)
                        } catch (e: FirebaseFirestoreException) { // FIXME: how to handle exception in stream?
                            println("### ERROR: ${e.message}")
                        }
                    }
                }
        }
    }


    @OptIn(FlowPreview::class)
    private fun startShelvesWithBookSync() {
        coroutineScope.launch {
            userLibraryRepository.getAllShelvesWithBooksPendingSyncStream()
                .debounce(5.seconds)
                .onStart {
                    println("### ON SWB SYNC START")
                }
                .onCompletion {
                    println("### ON SWB SYNC COMPLETION")
                }
                .collect { list ->
                    try {

                        println("### PENDING SWB: ${list.map { it.shelfId }}")
                        list.forEach { shelfBookJoin ->
                            val isDeleted = shelfBookJoin.isDeleted
                            println("### SYNCING SWB: ${shelfBookJoin.shelfId}")
                            networkDataSource.updateBookInShelf(
                                shelfId = shelfBookJoin.shelfId,
                                bookId = shelfBookJoin.bookId,
                                isBookInShelf = isDeleted.not()
                            )
                            userLibraryRepository.resetShelvesWithBooksSyncStatus(shelfId = shelfBookJoin.shelfId, bookId = shelfBookJoin.bookId)
                        }
                    } catch (e: FirebaseFirestoreException) { // FIXME: how to handle exception in stream?
                        println("### ERROR: ${e.message}")
                    }
                }
        }

        fun stopSync() {
            println("### stopSync()")
            job.cancel()
        }
    }
}