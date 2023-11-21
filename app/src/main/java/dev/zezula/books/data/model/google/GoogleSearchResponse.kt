package dev.zezula.books.data.model.google

import dev.zezula.books.data.model.book.BookFormData

data class GoogleSearchResponse(
    val items: List<GoogleSearchItem>? = null,
)

data class GoogleSearchItem(
    val id: String? = null,
    val volumeInfo: VolumeInfo? = null,
)

data class VolumeInfo(
    val title: String? = null,
    val subtitle: String? = null,
    val authors: List<String>? = null,
    val publisher: String? = null,
    val description: String? = null,
    val publishedDate: String? = null,
    val pageCount: Int? = null,
    val industryIdentifiers: List<IndustryIdentifier>? = null,
    val imageLinks: ImageLinks? = null,
)

data class IndustryIdentifier(
    // ISBN_13 or ISBN_10
    val type: String? = null,
    val identifier: String? = null,
)

data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null,
)

fun GoogleSearchItem.toBookFormData(): BookFormData {
    val book = this.volumeInfo
    val yearPublished = book?.publishedDate?.take(4)?.toIntOrNull()
    val positivePageCount = book?.pageCount?.takeIf { it > 0 }
    var isbn = book?.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier
    if (isbn == null) {
        isbn = book?.industryIdentifiers?.firstOrNull { it.type == "ISBN_10" }?.identifier
    }
    return BookFormData(
        title = book?.title,
        author = book?.authors?.joinToString(separator = ", "),
        publisher = book?.publisher,
        yearPublished = yearPublished,
        thumbnailLink = book?.imageLinks?.thumbnail?.replace("http://", "https://"),
        pageCount = positivePageCount,
        description = book?.description?.trim(),
        isbn = isbn,
    )
}
