package dev.zezula.books.data.network

import com.google.firebase.firestore.PropertyName

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
