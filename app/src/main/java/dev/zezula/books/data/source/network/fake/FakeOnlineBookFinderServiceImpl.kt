package dev.zezula.books.data.source.network.fake

import dev.zezula.books.data.model.FindBookOnlineResponse
import dev.zezula.books.data.source.network.OnlineBookFinderService

class FakeOnlineBookFinderServiceImpl : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): FindBookOnlineResponse {
        return FindBookOnlineResponse()
    }

    override suspend fun findBookForQueryOnline(query: String): FindBookOnlineResponse {
        return FindBookOnlineResponse()
    }
}
