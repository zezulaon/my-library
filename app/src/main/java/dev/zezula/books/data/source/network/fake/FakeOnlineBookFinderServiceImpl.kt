package dev.zezula.books.data.source.network.fake

import dev.zezula.books.data.model.FindBookOnlineResponse
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.source.network.OnlineBookFinderService

class FakeOnlineBookFinderServiceImpl : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): BookFormData? {
        return BookFormData()
    }

    override suspend fun findReviewsForIsbn(isbn: String): GoodreadsBook? {
        return GoodreadsBook()
    }

    override suspend fun findBookForQueryOnline(query: String): FindBookOnlineResponse {
        return FindBookOnlineResponse()
    }
}
