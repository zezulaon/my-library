package dev.zezula.books.data.model.google

import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.reference.Reference
import dev.zezula.books.data.model.reference.ReferenceId
import dev.zezula.books.util.currentDateInIso

data class GoogleSearchResponse(
    val items: List<GoogleSearchItem>? = null,
)

data class GoogleSearchItem(
    val id: String? = null,
    val volumeInfo: VolumeInfo? = null,
    val accessInfo: AccessInfo? = null,
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

data class AccessInfo(
    val accessViewStatus: String? = null,
    val publicDomain: Boolean? = null,
    val epub: AccessLink? = null,
    val pdf: AccessLink? = null,
)

data class AccessLink(
    val isAvailable: Boolean? = null,
    val downloadLink: String? = null,
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

fun ImageLinks.getHttpsThumbnailLink(): String? {
    return this.thumbnail?.replace("http://", "https://")
}

fun GoogleSearchItem.toBookFormData(): BookFormData {
    val book = this.volumeInfo
    val yearPublished = book?.publishedDate?.take(4)?.toIntOrNull()
    val positivePageCount = book?.pageCount?.takeIf { it > 0 }
    val isbn = book?.industryIdentifiers?.firstOrNull { (it.type == "ISBN_13" || it.type == "ISBN_10") }?.identifier
    return BookFormData(
        title = book?.title,
        author = book?.authors?.joinToString(separator = ", "),
        publisher = book?.publisher,
        yearPublished = yearPublished,
        thumbnailLink = book?.imageLinks?.getHttpsThumbnailLink(),
        pageCount = positivePageCount,
        description = book?.description?.trim(),
        isbn = isbn,
    )
}

fun GoogleSearchItem?.getReferences(): Map<String, String?> {
    val map = mutableMapOf(
        ReferenceId.GOOGLE_VOLUME_ID.id to this?.id,
        ReferenceId.GOOGLE_VOLUME_COVER_LINK.id to this?.volumeInfo?.imageLinks?.getHttpsThumbnailLink(),
    )
    // If the volume is in the public domain, check if there are links to PDF and EPUB
    if (this?.accessInfo?.publicDomain == true) {
        map[ReferenceId.GOOGLE_VOLUME_PDF_LINK.id] = this.accessInfo.pdf?.downloadLink
        map[ReferenceId.GOOGLE_VOLUME_EPUB_LINK.id] = this.accessInfo.epub?.downloadLink
    }
    return map
}
