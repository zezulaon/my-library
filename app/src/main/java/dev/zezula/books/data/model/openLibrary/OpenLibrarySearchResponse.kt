package dev.zezula.books.data.model.openLibrary

import com.google.gson.annotations.SerializedName
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.util.openLibraryEditionKeyThumbnailUrl
import dev.zezula.books.util.openLibraryIdThumbnailUrl

data class OpenLibrarySearchResponse(
    val docs: List<Doc>? = null,
)

data class Doc(
    val title: String? = null,
    @SerializedName("author_name") val author: List<String>? = null,
    @SerializedName("number_of_pages_median") val numberOfPages: Int? = null,
    @SerializedName("cover_edition_key") val coverEditionKey: String? = null,
    @SerializedName("cover_i") val coverId: Int? = null,
    @SerializedName("first_publish_year") val firstPublishedYear: Int? = null,
)

fun Doc.thumbnailUrl(): String? {
    return if (coverId != null) {
        openLibraryIdThumbnailUrl(coverId)
    } else if (coverEditionKey != null) {
        openLibraryEditionKeyThumbnailUrl(coverEditionKey)
    } else {
        null
    }
}

fun Doc.toBookFormData(): BookFormData = BookFormData(
    title = title.orEmpty(),
    author = author?.firstOrNull(),
    pageCount = numberOfPages,
    thumbnailLink = thumbnailUrl(),
    yearPublished = firstPublishedYear,
)
