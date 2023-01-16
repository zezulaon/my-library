package dev.zezula.books.data.model

import dev.zezula.books.data.model.goodreads.GoodreadsBook

data class FindBookOnlineResponse(
    val goodreadsBook: GoodreadsBook? = null,
)
