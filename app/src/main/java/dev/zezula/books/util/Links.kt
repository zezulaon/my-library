package dev.zezula.books.util

import dev.zezula.books.BuildConfig
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.reference.Reference
import dev.zezula.books.data.model.reference.ReferenceId

fun openLibraryIdThumbnailUrl(id: Int): String {
    return "https://covers.openlibrary.org/b/id/$id-M.jpg"
}

fun openLibraryEditionKeyThumbnailUrl(key: String): String {
    return "https://covers.openlibrary.org/b/olid/$key-M.jpg"
}

fun createAmazonSearchUrl(book: Book): String {
    val title = book.title
    val author = book.author
    var url = "${BuildConfig.ML_URL_AMAZON_SEARCH}$title"
    if (author != null) {
        url += " by $author"
    }
    return url
}

fun Reference.createUrl(): String? {
    if (value == null) return null
    return when (id) {
        ReferenceId.GOOGLE_VOLUME_ID.id -> "https://books.google.com/books?id=$value"
        ReferenceId.OL_BOOK_KEY.id -> "https://openlibrary.org/books/$value"
        ReferenceId.GOOGLE_VOLUME_EPUB_LINK.id -> value
        ReferenceId.GOOGLE_VOLUME_PDF_LINK.id -> value
        else -> null
    }
}
