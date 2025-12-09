package dev.zezula.books.core.model

import dev.zezula.books.core.utils.formatDate
import dev.zezula.books.core.utils.toSortingAuthor
import dev.zezula.books.core.utils.toSortingTitle

data class Book(
    val id: Id,
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val yearPublished: Int? = null,
    val pageCount: Int? = null,
    val thumbnailLink: String? = null,
    val userRating: Int? = null,
    val dateAdded: String,
) {

    @JvmInline
    value class Id(val value: String)

    val dateAddedFormatted: String = formatDate(dateAdded)

    // Titles without articles (a, an, the) for sorting
    val titleWithoutArticle by lazy { title?.toSortingTitle() }

    // Author's last name for sorting
    val authorLastName by lazy { author?.toSortingAuthor() }

    // TODO: Remove this once all old google books cover URLs are fixed in databases
    val thumbnailLinkSecurityFix: String? = thumbnailLink?.replace("http://", "https://")
}

data class BookFormData(
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val yearPublished: Int? = null,
    val pageCount: Int? = null,
    val userRating: Int? = null,
    val thumbnailLink: String? = null,
    // Subject is legacy property from older version of the app. It's not used right now.
    val subject: String? = null,
    // Binding is legacy property from older version of the app. It's not used right now.
    val binding: String? = null,
)

/**
 * Returns true if book has all important data set to non null values
 */
fun BookFormData.isComplete(): Boolean {
    return title != null &&
        author != null &&
        isbn != null &&
        publisher != null &&
        yearPublished != null &&
        pageCount != null &&
        thumbnailLink != null
}

/**
 * Returns new copy of the book that has all null values replaced by the values from the [other] book.
 * @param other Book to copy non null values from
 */
fun BookFormData.updateNullValues(other: BookFormData?): BookFormData {
    if (other == null) return this
    return BookFormData(
        title = title ?: other.title,
        author = author ?: other.author,
        description = description ?: other.description,
        isbn = isbn ?: other.isbn,
        publisher = publisher ?: other.publisher,
        yearPublished = yearPublished ?: other.yearPublished,
        pageCount = pageCount ?: other.pageCount,
        thumbnailLink = thumbnailLink ?: other.thumbnailLink,
    )
}

val previewBooks = listOf(
    Book(
        id = Book.Id("1"),
        title = "Hobit",
        author = "J. R. R. Tolkien",
        description = "Hobit description",
        isbn = "987789555",
        publisher = "Publisher 1",
        yearPublished = 2001,
        pageCount = 152,
        thumbnailLink = null,
        userRating = 4,
        dateAdded = "2023-01-05T17:43:25.629",
    ),
    Book(
        id = Book.Id("2"),
        title = "Neverwhere",
        author = "N. Gaiman",
        description = "Neverwhere description",
        isbn = "987789554",
        publisher = "Publisher 2",
        yearPublished = 2001,
        pageCount = 152,
        thumbnailLink = null,
        userRating = 4,
        dateAdded = "2023-01-05T17:43:25.629",
    ),
)
