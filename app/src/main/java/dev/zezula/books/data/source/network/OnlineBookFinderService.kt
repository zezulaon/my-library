package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.goodreads.GoodreadsBook

interface OnlineBookFinderService {

    suspend fun findBookForIsbnOnline(isbn: String): BookFormData?

    suspend fun findReviewsForIsbn(isbn: String): GoodreadsBook?

    suspend fun findBookForQueryOnline(query: String): List<BookFormData>
}
