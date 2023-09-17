package dev.zezula.books.data.model.goodreads

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false)
data class Work @JvmOverloads constructor(

    @field:Element(required = false)
    var ratings_count: Int? = null,

    @field:Element(required = false)
    var text_reviews_count: Int? = null,

    @field:Element(required = false)
    var best_book: GoodreadsBook? = null,
)
