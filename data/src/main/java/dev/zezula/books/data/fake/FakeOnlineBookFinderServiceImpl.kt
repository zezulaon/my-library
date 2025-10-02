package dev.zezula.books.data.fake

import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.services.OnlineBookFinderService

class FakeOnlineBookFinderServiceImpl : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): BookFormData {
        return BookFormData()
    }

    override suspend fun findBookForQueryOnline(query: String): List<BookFormData> {
        return emptyList()
    }

    override suspend fun findBookCoverLinkForIsbn(isbn: String): String? {
        return null
    }
}