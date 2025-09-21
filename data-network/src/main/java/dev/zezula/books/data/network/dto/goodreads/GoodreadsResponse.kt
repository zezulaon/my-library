package dev.zezula.books.data.network.dto.goodreads

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false)
data class GoodreadsResponse @JvmOverloads constructor(

    @field:Element(required = false)
    var book: GoodreadsBook? = null,

    @field:Element(required = false)
    var search: Search? = null,
)
