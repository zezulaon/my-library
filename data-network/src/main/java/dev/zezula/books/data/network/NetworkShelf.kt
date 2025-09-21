package dev.zezula.books.data.network

import com.google.firebase.firestore.PropertyName

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkShelf(
    val id: String? = null,
    val dateAdded: String? = null,
    val title: String? = null,

    @get:PropertyName(FIELD_IS_DELETED)
    val isDeleted: Boolean? = null,
    val lastModifiedTimestamp: String? = null,
)

