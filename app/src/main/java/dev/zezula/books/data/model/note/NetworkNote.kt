package dev.zezula.books.data.model.note

import com.google.firebase.firestore.PropertyName
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.source.network.FIELD_IS_DELETED
import kotlinx.datetime.Clock
import timber.log.Timber

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

fun NetworkNote.asEntity(): NoteEntity? {
    return if (id == null || bookId == null || dateAdded == null || text == null) {
        Timber.e("ID, bookId, dateAdded, or text is null: id=$id, bookId=$bookId, dateAdded=$dateAdded, text=$text")
        null
    } else {
        NoteEntity(
            id = Note.Id(id),
            bookId = Book.Id(bookId),
            dateAdded = dateAdded,
            text = text,
            page = page,
            type = type,
            isDeleted = isDeleted == true,
            lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
        )
    }
}
