package dev.zezula.books.util

fun openLibraryIdThumbnailUrl(id: Int): String {
    return "https://covers.openlibrary.org/b/id/$id-M.jpg"
}

fun openLibraryEditionKeyThumbnailUrl(key: String): String {
    return "https://covers.openlibrary.org/b/olid/$key-M.jpg"
}
