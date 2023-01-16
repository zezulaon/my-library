package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.FindBookOnlineResponse

interface OnlineBookFinderService {

    suspend fun findBookOnline(isbn: String): FindBookOnlineResponse
}