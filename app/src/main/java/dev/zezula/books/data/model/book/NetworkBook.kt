package dev.zezula.books.data.model.book

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkBook(
    val id: String? = null,
    val dateAdded: String? = null,
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val yearPublished: Int? = null,
    val pageCount: Int? = null,
    val thumbnailLink: String? = null,
    val userRating: Int? = null,
)
