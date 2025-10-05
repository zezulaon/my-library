package dev.zezula.books.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.zezula.books.core.model.Book

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: Book.Id,
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
    val lastModifiedTimestamp: String? = null,
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
