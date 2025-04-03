package dev.zezula.books.data.model.shelf

import com.google.firebase.firestore.PropertyName
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.source.network.FIELD_BOOK_ID
import dev.zezula.books.data.source.network.FIELD_IS_DELETED
import dev.zezula.books.data.source.network.FIELD_SHELF_ID
import kotlinx.datetime.Clock

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkShelfWithBook(

    @PropertyName(FIELD_BOOK_ID)
    val bookId: String? = null,

    @PropertyName(FIELD_SHELF_ID)
    val shelfId: String? = null,

    @get:PropertyName(FIELD_IS_DELETED)
    val isDeleted: Boolean? = null,

    val lastModifiedTimestamp: String? = null,
)

// FIXME: Tmp solution. Invalid state should be just logged and entity insertion skipped
fun NetworkShelfWithBook.asEntity() = ShelfWithBookEntity(
    bookId = checkNotNull(bookId) { "NetworkShelfWithBook bookId is null" }.let { Book.Id(it) },
    shelfId = checkNotNull(shelfId) { "NetworkShelfWithBook shelfId is null" }.let { Shelf.Id(it) },
    isDeleted = isDeleted == true,
    lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
)
