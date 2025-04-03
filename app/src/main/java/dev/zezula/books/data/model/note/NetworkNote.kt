package dev.zezula.books.data.model.note

import com.google.firebase.firestore.PropertyName
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.source.network.FIELD_IS_DELETED
import kotlinx.datetime.Clock

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkNote(
    val id: String? = null,
    val bookId: String? = null,
    val dateAdded: String? = null,
    val text: String? = null,
    val page: Int? = null,
    val type: String? = null,

    @get:PropertyName(FIELD_IS_DELETED)
    val isDeleted: Boolean? = null,

    val lastModifiedTimestamp: String? = null,
)

// FIXME: review these checkNotNull calls
fun NetworkNote.asEntity() = NoteEntity(
    id = checkNotNull(id) { "NetworkNote id is null" }.let { Note.Id(it) },
    bookId = checkNotNull(bookId) { "NetworkNote bookId is null" }.let { Book.Id(it) },
    dateAdded = checkNotNull(dateAdded) { "NetworkNote dateAdded is null" },
    text = checkNotNull(text) { "NetworkNote text is null" },
    page = page,
    type = type,
    isDeleted = isDeleted == true,
    lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
)