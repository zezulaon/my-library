package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.FindBookOnlineResponse

interface OnlineBookFinderService {

    suspend fun findBookForIsbnOnline(isbn: String): FindBookOnlineResponse

    suspend fun findBookForQueryOnline(query: String): FindBookOnlineResponse
}
