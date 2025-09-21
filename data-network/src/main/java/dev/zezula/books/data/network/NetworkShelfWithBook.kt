package dev.zezula.books.data.network

import com.google.firebase.firestore.PropertyName

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
