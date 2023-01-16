package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.FindBookOnlineResponse
import dev.zezula.books.util.removeHtmlTags

class OnlineBookFinderServiceImpl(
    private val goodreadsApi: GoodreadsApi,
) : OnlineBookFinderService {

    override suspend fun findBookOnline(isbn: String): FindBookOnlineResponse {
        val goodreadsBook = goodreadsApi.findBookOrNull(isbn)
        val descWithoutHtmlTags = goodreadsBook?.description?.removeHtmlTags()
        return FindBookOnlineResponse(
            goodreadsBook = goodreadsBook?.copy(description = descWithoutHtmlTags)
        )
    }
}