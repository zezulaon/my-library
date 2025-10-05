package dev.zezula.books.data.network.dto.goodreads

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
data class Search @JvmOverloads constructor(

    @field:ElementList(required = false)
    var results: List<Work>? = null,
)
