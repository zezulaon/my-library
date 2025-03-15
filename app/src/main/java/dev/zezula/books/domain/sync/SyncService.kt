package dev.zezula.books.domain.sync

import com.google.firebase.firestore.FirebaseFirestoreException
import dev.zezula.books.data.SyncLibraryRepository
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

// FIXME: review the syncing
class SyncService(
    private val syncLibraryRepository: SyncLibraryRepository,
    private val networkDataSource: NetworkDataSource,
) {

    private val syncStarted: AtomicBoolean = AtomicBoolean(false)

    // FIXME: The sync service should be a singleton whose job can be launched only once. It should handle exceptions/error during sync (it should
    //  not crash the app or stop the sync service).
    private val job = SupervisorJob()

    private val coroutineContext = Dispatchers.IO + job
    private val coroutineScope = CoroutineScope(coroutineContext)

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
            syncLibraryRepository.getAllBooksPendingSyncFlow()
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
                        println("### SYNCING BOOK: ${book.title}")
                        try {
                            networkDataSource.addOrUpdateBook(book)

                            // FIXME: improve so the null check is not needed (pass id from the repository?)
                            val bookId = checkNotNull(book.id) { "Book ID is null" }
                            syncLibraryRepository.resetBookPendingSyncStatus(bookId)
                        } catch (e: FirebaseFirestoreException) { // FIXME: how to handle exception in stream?
                            println("### ERROR: ${e.message}")
                        }
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    private fun startShelvesSync() {
        coroutineScope.launch {
            syncLibraryRepository.getAllShelvesPendingSyncFlow()
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
                        println("### SYNCING SHELF: ${shelf.title}")
                        try {
                            networkDataSource.addOrUpdateShelf(shelf)

                            val shelfId = checkNotNull(shelf.id) { "Shelf ID is null" }
                            syncLibraryRepository.resetShelfPendingSyncStatus(shelfId)
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

            syncLibraryRepository.getAllNotesPendingSyncFlow()
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
                        try {
                            networkDataSource.addOrUpdateNote(note)

                            val noteId = checkNotNull(note.id) { "Note ID is null" }
                            syncLibraryRepository.resetNotePendingSyncStatus(noteId)
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
            syncLibraryRepository.getAllShelvesWithBooksPendingSyncFlow()
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
                        list.forEach { shelfWithBook ->
                            println("### SYNCING SWB: $shelfWithBook")

                            val shelfId = checkNotNull(shelfWithBook.shelfId) { "Shelf ID is null" }
                            val bookId = checkNotNull(shelfWithBook.bookId) { "Book ID is null" }
                            networkDataSource.updateBookInShelf(shelfWithBook)
                            syncLibraryRepository.resetShelfWithBookPendingSyncStatus(shelfId = shelfId, bookId = bookId)
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