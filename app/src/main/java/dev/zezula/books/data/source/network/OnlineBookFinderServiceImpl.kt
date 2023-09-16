package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.FindBookOnlineResponse
import dev.zezula.books.util.removeHtmlTags

class OnlineBookFinderServiceImpl(
    private val goodreadsApi: GoodreadsApi,
    private val openLibraryApi: OpenLibraryApi,
) : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): FindBookOnlineResponse {
        val goodreadsBook = goodreadsApi.findBookOrNull(isbn)
        val descWithoutHtmlTags = goodreadsBook?.description?.removeHtmlTags()
        return FindBookOnlineResponse(
            goodreadsBook = goodreadsBook?.copy(description = descWithoutHtmlTags),
        )
    }

    override suspend fun findBookForQueryOnline(query: String): FindBookOnlineResponse {
        val openLibrarySearchResponse = openLibraryApi.searchByQuery(query)
        return FindBookOnlineResponse(
            openLibrary = openLibrarySearchResponse,
        )
    }
}
