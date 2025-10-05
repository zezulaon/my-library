package dev.zezula.books.domain.services

import dev.zezula.books.core.model.BookFormData

interface OnlineBookFinderService {

    suspend fun findBookForIsbnOnline(isbn: String): BookFormData?

    suspend fun findBookCoverLinkForIsbn(isbn: String): String?

    suspend fun findBookForQueryOnline(query: String): List<BookFormData>
}
