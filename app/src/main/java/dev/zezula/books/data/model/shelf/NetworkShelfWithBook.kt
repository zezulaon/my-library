package dev.zezula.books.data.model.shelf

import com.google.firebase.firestore.PropertyName
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.source.network.FIELD_BOOK_ID
import dev.zezula.books.data.source.network.FIELD_IS_DELETED
import dev.zezula.books.data.source.network.FIELD_SHELF_ID
import kotlinx.datetime.Clock
import timber.log.Timber

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

fun NetworkShelfWithBook.asEntity(): ShelfWithBookEntity? {
    return if (bookId == null || shelfId == null) {
        Timber.e("Book ID or Shelf ID is null: bookId=$bookId, shelfId=$shelfId")
        null
    } else {
        ShelfWithBookEntity(
            bookId = Book.Id(bookId),
            shelfId = Shelf.Id(shelfId),
            isDeleted = isDeleted == true,
            lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
        )
    }
}
