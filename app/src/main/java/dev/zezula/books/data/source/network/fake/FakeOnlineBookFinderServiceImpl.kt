package dev.zezula.books.data.source.network.fake

import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.model.reference.Reference
import dev.zezula.books.data.source.network.ApiType
import dev.zezula.books.data.source.network.OnlineBookFinderService

class FakeOnlineBookFinderServiceImpl : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): BookFormData? {
        return BookFormData()
    }

    override suspend fun findReviewsForIsbn(isbn: String): GoodreadsBook? {
        return GoodreadsBook()
    }

    override suspend fun findBookForQueryOnline(query: String): List<BookFormData> {
        return emptyList()
    }

    override suspend fun findReferencesForIsbn(isbn: String, apiType: ApiType): Map<String, String?> {
        return emptyMap()
    }
}
