package dev.zezula.books.data.model.book

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
    val isbn: String? = null,
    val publisher: String? = null,
    val yearPublished: Int? = null,
    val thumbnailLink: String? = null,
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
    )
}

fun fromNetworkBook(networkBook: NetworkBook): BookEntity {
    checkNotNull(networkBook.id) { "Book needs [id] property" }
    checkNotNull(networkBook.title) { "Book needs [title] property" }
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
        thumbnailLink = networkBook.thumbnailLink
    )
}