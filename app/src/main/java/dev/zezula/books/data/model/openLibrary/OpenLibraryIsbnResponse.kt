package dev.zezula.books.data.model.openLibrary

import com.google.gson.annotations.SerializedName

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
)

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
