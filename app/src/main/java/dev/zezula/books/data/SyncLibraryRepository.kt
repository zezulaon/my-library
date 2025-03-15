package dev.zezula.books.data

import dev.zezula.books.data.model.book.NetworkBook
import dev.zezula.books.data.model.note.NetworkNote
import dev.zezula.books.data.model.shelf.NetworkShelf
import dev.zezula.books.data.model.shelf.NetworkShelfWithBook
import kotlinx.coroutines.flow.Flow

interface SyncLibraryRepository {

    fun getAllBooksPendingSyncFlow(): Flow<List<NetworkBook>>

    suspend fun resetBookPendingSyncStatus(bookId: String)

    fun getAllShelvesWithBooksPendingSyncFlow(): Flow<List<NetworkShelfWithBook>>

    suspend fun resetShelfWithBookPendingSyncStatus(shelfId: String, bookId: String)

    fun getAllNotesPendingSyncFlow(): Flow<List<NetworkNote>>

    suspend fun resetNotePendingSyncStatus(noteId: String)

    fun getAllShelvesPendingSyncFlow(): Flow<List<NetworkShelf>>

    suspend fun resetShelfPendingSyncStatus(shelfId: String)
}
