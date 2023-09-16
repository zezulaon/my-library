package dev.zezula.books.data.model

import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.model.openLibrary.OpenLibrarySearchResponse

data class FindBookOnlineResponse(
    val goodreadsBook: GoodreadsBook? = null,
    val openLibrary: OpenLibrarySearchResponse? = null,
)
