package dev.zezula.books.data

import com.google.firebase.firestore.FirebaseFirestoreException
import dev.zezula.books.data.model.book.asNetworkBook
import dev.zezula.books.data.model.note.asNetworkNote
import dev.zezula.books.data.model.shelf.asNetworkShelf
import dev.zezula.books.data.model.shelf.asNetworkShelfWithBook
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.NoteDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import dev.zezula.books.data.source.db.ShelfDao
import dev.zezula.books.data.source.network.NetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

class BackupService(
    private val coroutineScope: CoroutineScope,
    private val networkDataSource: NetworkDataSource,
    private val bookDao: BookDao,
    private val shelfDao: ShelfDao,
    private val noteDao: NoteDao,
    private val shelfAndBookDao: ShelfAndBookDao,
) {
    private val serviceStarted: AtomicBoolean = AtomicBoolean(false)

    fun startBackupService() {
        Timber.d("Starting backup service")
        if (serviceStarted.getAndSet(true)) {
            Timber.d("Backup service is already in progress - skipping")
            return
        }

        startBooksSync()
        startShelvesSync()
        startShelvesWithBookSync()
        startNotesSync()
    }

    private fun startBooksSync() {
        coroutineScope.launch {
            bookDao
                .getAllBooksPendingSyncStream()
                .onEachPending { bookEntity ->
                    networkDataSource.addOrUpdateBook(bookEntity.asNetworkBook())
                    bookDao.resetBookPendingSyncStatus(bookEntity.id)
                }
        }
    }

    private fun startShelvesSync() {
        coroutineScope.launch {
            shelfDao
                .getAllShelvesPendingSyncStream()
                .onEachPending { shelfEntity ->
                    networkDataSource.addOrUpdateShelf(shelfEntity.asNetworkShelf())
                    shelfDao.resetShelfPendingSyncStatus(shelfEntity.id)
                }
        }
    }

    private fun startNotesSync() {
        coroutineScope.launch {
            noteDao
                .getAllNotesPendingSyncStream()
                .onEachPending { noteEntity ->
                    networkDataSource.addOrUpdateNote(noteEntity.asNetworkNote())
                    noteDao.resetNotePendingSyncStatus(noteEntity.id)
                }
        }
    }

    private fun startShelvesWithBookSync() {
        coroutineScope.launch {
            shelfAndBookDao
                .getAllShelvesWithBooksPendingSyncFlow()
                .onEachPending { shelfWithBook ->
                    networkDataSource.updateBookInShelf(shelfWithBook.asNetworkShelfWithBook())
                    shelfAndBookDao.resetShelfWithBookPendingSyncStatus(shelfId = shelfWithBook.shelfId, bookId = shelfWithBook.bookId)
                }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend inline fun <reified T> Flow<List<T>>.onEachPending(crossinline backupBlock: suspend (T) -> Unit) {
        val entityName = T::class.simpleName
        this.debounce(5.seconds)
            .onStart {
                Timber.d("[$entityName] - Backup started")
            }
            .onCompletion { cause ->
                Timber.d("[$entityName] - Backup completed")
                if (cause != null) {
                    Timber.e(cause)
                }
            }
            .collect { list ->
                Timber.d("[$entityName] - Pending items: ${list.size}")
                try {
                    list.forEach {
                        backupBlock(it)
                    }
                } catch (e: FirebaseFirestoreException) {
                    Timber.e(e, "[$entityName] - Error during backup")
                }
            }
    }
}
