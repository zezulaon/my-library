package dev.zezula.books.data.model.book

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Book(
    val id: String,
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val yearPublished: Int? = null,
    val pageCount: Int? = null,
    val thumbnailLink: String? = null,
    val dateAdded: String,
) {
    val dateAddedFormatted: String =
        LocalDateTime.parse(dateAdded).format(DateTimeFormatter.ofPattern("d/MM/uuuu"))
}

data class BookFormData(
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val yearPublished: Int? = null,
    val pageCount: Int? = null,
    val thumbnailLink: String? = null,
)

val previewBooks = listOf(
    Book(
        id = "1",
        title = "Hobit",
        author = "J. R. R. Tolkien",
        description = "Hobit desc",
        isbn = "987789555",
        publisher = "Publisher 1",
        yearPublished = 2001,
        pageCount = 152,
        thumbnailLink = null,
        dateAdded = "2023-01-05T17:43:25.629",
    ),
    Book(
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
