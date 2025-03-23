package dev.zezula.books.data.model.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val dateAdded: String,
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    // Subject is legacy property from older version of the app. It's not used right now.
    val subject: String? = null,
    // Binding is legacy property from older version of the app. It's not used right now.
    val binding: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val yearPublished: Int? = null,
    val thumbnailLink: String? = null,
    val userRating: Int? = null,
    val pageCount: Int? = null,
    val isInLibrary: Boolean = false,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isPendingSync: Boolean = false,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isDeleted: Boolean = false,
)

fun BookEntity.asExternalModel(): Book {
    return Book(
        id = id,
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
    )
}

fun BookEntity.asNetworkBook(): NetworkBook {
    // [isInLibrary] is not needed since all [NetworkBook]s are already in library.
    return NetworkBook(
        id = id,
        dateAdded = dateAdded,
        title = title,
        author = author,
        description = description,
        subject = subject,
        binding = binding,
        isbn = isbn,
        publisher = publisher,
        yearPublished = yearPublished,
        thumbnailLink = thumbnailLink,
        userRating = userRating,
        pageCount = pageCount,
        isDeleted = isDeleted,
    )
}

val previewBookEntities = listOf(
    BookEntity(
        id = "1",
        title = "Hobit",
        author = "J. R. R. Tolkien",
        description = "Hobit desc",
        isbn = "987789555",
        publisher = "Publisher 1",
        yearPublished = 2001,
        pageCount = 152,
        thumbnailLink = null,
        dateAdded = "2022-01-05T17:43:25.629",
    ),
    BookEntity(
        id = "2",
        title = "Neverwhere",
        author = "N. Gaiman",
        description = "Neverwhere description",
        isbn = "987789554",
        publisher = "Publisher 2",
        yearPublished = 2001,
        pageCount = 152,
        thumbnailLink = null,
        dateAdded = "2023-01-05T17:43:25.629",
    ),
)
