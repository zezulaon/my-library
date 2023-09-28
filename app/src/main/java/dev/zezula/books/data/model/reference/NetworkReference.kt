package dev.zezula.books.data.model.reference

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkReference(
    val id: String? = null,
    val bookId: String? = null,
    val value: String? = null,
    val dateUpdated: String? = null,
)
