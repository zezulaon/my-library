package dev.zezula.books.data.model.shelf

import com.google.firebase.firestore.PropertyName

const val bookIdProperty = "bookId"
const val shelfIdProperty = "shelfId"

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkShelfWithBook(

    @PropertyName(bookIdProperty)
    val bookId: String? = null,

    @PropertyName(shelfIdProperty)
    val shelfId: String? = null,
)