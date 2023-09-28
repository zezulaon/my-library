package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.model.reference.Reference

interface OnlineBookFinderService {

    suspend fun findBookForIsbnOnline(isbn: String): BookFormData?

    suspend fun findReviewsForIsbn(isbn: String): GoodreadsBook?

    suspend fun findBookForQueryOnline(query: String): List<BookFormData>

    suspend fun findReferencesForIsbn(isbn: String, apiType: ApiType): Map<String, String?>
}

enum class ApiType {
    GOOGLE,
    OPEN_LIBRARY,
    GOODREADS,
}
