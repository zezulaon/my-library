package dev.zezula.books.data.network.dto.goodreads

import dev.zezula.books.core.model.BookFormData
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
data class GoodreadsBook @JvmOverloads constructor(

    @field:Element(required = false)
    var title: String? = null,

    @field:Element(required = false)
    var isbn: String? = null,

    @field:Element(required = false)
    var isbn13: String? = null,

    @field:Element(required = false)
    var image_url: String? = null,

    @field:Element(required = false)
    var publication_year: String? = null,

    @field:Element(required = false)
    var publisher: String? = null,

    @field:Element(required = false)
    var description: String? = null,

    @field:Element(required = false)
    var average_rating: String? = null,

    @field:Element(required = false)
    var work: Work? = null,

    @field:Element(required = false)
    var num_pages: Int? = null,

    @field:ElementList(required = false)
    var reviews: List<GoodReadsReview>? = null,

    @field:ElementList(required = false)
    var authors: List<Author>? = null,

    @field:Element(required = false)
    var author: Author? = null,
)

fun GoodreadsBook.toBookFormData(): BookFormData {
    return BookFormData(
        title = this.title ?: "",
        author = this.author(),
        publisher = this.publisher,
        yearPublished = this.publication_year?.toIntOrNull(),
        thumbnailLink = this.thumbnailUrl(),
        pageCount = this.num_pages,
        description = this.description?.trim(),
        isbn = this.isbn13 ?: this.isbn,
    )
}

fun GoodreadsBook.thumbnailUrl(): String? {
    val tmpUrl = image_url
    return if (tmpUrl != null && tmpUrl.contains("nophoto").not()) {
        tmpUrl
    } else {
        null
    }
}

private fun GoodreadsBook.author(): String? {
    return if (authors.isNullOrEmpty().not()) {
        this.authors?.mapToAuthor()
    } else {
        this.author?.name
    }
}

private fun List<Author>?.mapToAuthor(): String? {
    return this?.filter {
        "writer".equals(
            it.role,
            true,
        ) || it.role == null
    } // Filter out all "non-author" roles (eg translators)
        ?.mapNotNull { it.name }
        ?.joinToString(separator = ", ")
}
