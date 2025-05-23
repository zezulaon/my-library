package dev.zezula.books.data.model.book

import com.google.firebase.firestore.PropertyName
import dev.zezula.books.data.source.network.FIELD_IS_DELETED
import kotlinx.datetime.Clock
import timber.log.Timber

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

    // Subject is legacy property from older version of the app. It's not used right now.
    val subject: String? = null,

    // Binding is legacy property from older version of the app. It's not used right now.
    val binding: String? = null,

    @get:PropertyName(FIELD_IS_DELETED)
    val isDeleted: Boolean? = null,

    val lastModifiedTimestamp: String? = null,
)

fun NetworkBook.asEntity(): BookEntity? {
    if (id == null || dateAdded == null) {
        Timber.e("ID or dateAdded is null: id=$id, dateAdded=$dateAdded")
        return null
    } else {
        return BookEntity(
            id = Book.Id(id),
            dateAdded = dateAdded,
            title = title,
            author = author,
            description = description,
            isbn = isbn,
            publisher = publisher,
            yearPublished = yearPublished,
            pageCount = pageCount,
            thumbnailLink = thumbnailLink,
            userRating = userRating,
            subject = subject,
            binding = binding,
            isDeleted = isDeleted == true,
            isInLibrary = true,
            lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
        )
    }
}
