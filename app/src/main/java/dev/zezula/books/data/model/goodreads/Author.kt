package dev.zezula.books.data.model.goodreads

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false)
data class Author @JvmOverloads constructor(

    @field:Element(required = false)
    var name: String? = null,

    @field:Element(required = false)
    var role: String? = null,
)