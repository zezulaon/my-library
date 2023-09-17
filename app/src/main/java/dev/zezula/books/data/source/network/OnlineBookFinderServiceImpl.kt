package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.FindBookOnlineResponse
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.model.goodreads.toBookFormData
import dev.zezula.books.data.model.openLibrary.author
import dev.zezula.books.data.model.openLibrary.publisher
import dev.zezula.books.data.model.openLibrary.yearPublished
import timber.log.Timber

class OnlineBookFinderServiceImpl(
    private val goodreadsApi: GoodreadsApi,
    private val openLibraryApi: OpenLibraryApi,
) : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): BookFormData? {
        Timber.d("Searching book for isbn: $isbn")
        val goodreadsBook = goodreadsApi.findBookByIsbnOrNull(isbn)
        Timber.d("Goodreads book: $goodreadsBook")
        val openLibraryBook = openLibraryApi.searchByBibKeyIsbn(isbn)?.values?.firstOrNull()
        Timber.d("OpenLibrary book: $openLibraryBook")

        if (goodreadsBook == null && openLibraryBook == null) {
            return null
        }

        val grFormData = goodreadsBook?.toBookFormData()
        return BookFormData(
            title = grFormData?.title ?: openLibraryBook?.title.orEmpty(),
            author = openLibraryBook?.author() ?: grFormData?.author,
            isbn = isbn,
            thumbnailLink = openLibraryBook?.cover?.medium ?: grFormData?.thumbnailLink,
            pageCount = grFormData?.pageCount,
            yearPublished = grFormData?.yearPublished ?: openLibraryBook?.yearPublished(),
            publisher = grFormData?.publisher ?: openLibraryBook?.publisher(),
        )
    }

    override suspend fun findReviewsForIsbn(isbn: String): GoodreadsBook? {
        return goodreadsApi.findReviewsOrNull(isbn)
    }

    override suspend fun findBookForQueryOnline(query: String): FindBookOnlineResponse {
        val openLibrarySearchResponse = openLibraryApi.searchByQuery(query)
        return FindBookOnlineResponse(
            openLibrary = openLibrarySearchResponse,
        )
    }
}
