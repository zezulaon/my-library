package dev.zezula.books.data.network.dto.goodreads

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false)
data class GoodReadsReview @JvmOverloads constructor(

    @field:Element(required = false)
    var rating: Int? = null,

    @field:Element(required = false)
    var body: String? = null,

    @field:Element(required = false)
    var votes: Int? = null,

    @field:Element(required = false)
    var link: String? = null,

    @field:Element(required = false)
    var spoiler_flag: Boolean? = null,

    @field:Element(required = false)
    var user: User? = null,
)
