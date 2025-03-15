package dev.zezula.books.data.model.shelf

import com.google.firebase.firestore.PropertyName
import dev.zezula.books.data.source.network.FIELD_IS_DELETED

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkShelf(
    val id: String? = null,
    val dateAdded: String? = null,
    val title: String? = null,

    @get:PropertyName(FIELD_IS_DELETED)
    val isDeleted: Boolean? = null,
)
