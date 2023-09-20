package dev.zezula.books.data.model.openLibrary

import com.google.gson.annotations.SerializedName
import dev.zezula.books.data.model.book.BookFormData

data class OpenLibraryIsbnResponse(
    val title: String? = null,
    val publishers: List<String>? = null,
    val covers: List<Int>? = null,
)

data class OpenLibraryBibKeyItem(
    val title: String? = null,
    val authors: List<OpenLibraryAuthor>? = null,
    val publishers: List<OpenLibraryPublisher>? = null,
    val cover: OpenLibraryCover? = null,
    @SerializedName("publish_date") val publishDate: String? = null,
    @SerializedName("number_of_pages") val numberOfPages: Int? = null,
    val identifiers: Map<String, List<String>>,
)

fun OpenLibraryBibKeyItem.toBookFormData(): BookFormData {
    val yearPublished = this.publishDate?.take(4)?.toIntOrNull()
    val positivePageCount = this.numberOfPages?.takeIf { it > 0 }
    val isbn = this.identifiers["isbn_13"]?.firstOrNull() ?: this.identifiers["isbn_10"]?.firstOrNull()
    return BookFormData(
        title = this.title,
        author = this.author(),
        publisher = this.publisher(),
        yearPublished = yearPublished,
        thumbnailLink = this.cover?.medium,
        pageCount = positivePageCount,
        isbn = isbn,
    )
}

/**
 * Check if the given [isbn] is in the list of identifiers
 * @param isbn ISBN to check
 */
fun OpenLibraryBibKeyItem.containsIsbn(isbn: String): Boolean {
    // Turns map of identifiers (where key is the type of identifier, and value is list of identifiers) into a list of all identifiers
    val allIdentifiers = identifiers.values.flatten()
    return allIdentifiers.contains(isbn)
}

data class OpenLibraryAuthor(
    val name: String? = null,
)

data class OpenLibraryPublisher(
    val name: String? = null,
)

data class OpenLibraryCover(
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
)

fun OpenLibraryBibKeyItem.author(): String? {
    return this.authors?.mapNotNull { it.name }?.joinToString()
}

fun OpenLibraryBibKeyItem.yearPublished(): Int? {
    return this.publishDate?.take(4)?.toIntOrNull()
}

fun OpenLibraryBibKeyItem.publisher(): String? {
    return this.publishers?.firstOrNull()?.name
}
