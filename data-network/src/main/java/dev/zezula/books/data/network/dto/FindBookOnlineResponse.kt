package dev.zezula.books.data.network.dto

import dev.zezula.books.data.network.dto.goodreads.GoodreadsBook
import dev.zezula.books.data.network.dto.openLibrary.OpenLibrarySearchResponse

data class FindBookOnlineResponse(
    val goodreadsBook: GoodreadsBook? = null,
    val openLibrary: OpenLibrarySearchResponse? = null,
)