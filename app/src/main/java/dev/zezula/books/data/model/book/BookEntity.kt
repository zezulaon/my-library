package dev.zezula.books.data.model.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    // TODO: [LibraryBookEntity] would be better place for this property.
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
)

fun BookEntity.asExternalModel(): Book {
    return Book(
        id = this.id,
        dateAdded = this.dateAdded,
        title = this.title,
        author = this.author,
        description = this.description,
        isbn = this.isbn,
        publisher = this.publisher,
        yearPublished = this.yearPublished,
        pageCount = this.pageCount,
        thumbnailLink = this.thumbnailLink,
        userRating = this.userRating,
    )
}

fun BookEntity.asNetworkBook(): NetworkBook {
    return NetworkBook(
        id = this.id,
        dateAdded = this.dateAdded,
        title = this.title,
        author = this.author,
        description = this.description,
        isbn = this.isbn,
        publisher = this.publisher,
        yearPublished = this.yearPublished,
        pageCount = this.pageCount,
        thumbnailLink = this.thumbnailLink,
        userRating = this.userRating,
    )
}

fun fromNetworkBook(networkBook: NetworkBook): BookEntity {
    checkNotNull(networkBook.id) { "Book needs [id] property" }
    checkNotNull(networkBook.dateAdded) { "Book needs [dateAdded] property" }
    return BookEntity(
        id = networkBook.id,
        dateAdded = networkBook.dateAdded,
        title = networkBook.title,
        author = networkBook.author,
        description = networkBook.description,
        isbn = networkBook.isbn,
        publisher = networkBook.publisher,
        yearPublished = networkBook.yearPublished,
        pageCount = networkBook.pageCount,
        thumbnailLink = networkBook.thumbnailLink,
        userRating = networkBook.userRating,
    )
}

fun fromBookFormData(id: String, dateAdded: String, bookFormData: BookFormData): BookEntity {
    return BookEntity(
        id = id,
        dateAdded = dateAdded,
        title = bookFormData.title,
        author = bookFormData.author,
        description = bookFormData.description,
        isbn = bookFormData.isbn,
        publisher = bookFormData.publisher,
        yearPublished = bookFormData.yearPublished,
        pageCount = bookFormData.pageCount,
        thumbnailLink = bookFormData.thumbnailLink,
        userRating = bookFormData.userRating,
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
