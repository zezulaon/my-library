package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.data.model.book.isComplete
import dev.zezula.books.data.model.book.updateNullValues
import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.model.goodreads.thumbnailUrl
import dev.zezula.books.data.model.goodreads.toBookFormData
import dev.zezula.books.data.model.google.thumbnailUrl
import dev.zezula.books.data.model.google.toBookFormData
import dev.zezula.books.data.model.openLibrary.toBookFormData
import timber.log.Timber

class OnlineBookFinderServiceImpl(
    private val goodreadsApi: GoodreadsApi,
    private val openLibraryApi: OpenLibraryApi,
    private val googleApi: GoogleApi,
) : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): BookFormData? {
        Timber.d("Searching book for isbn: $isbn")

        // Try to get book from Google API
        val googleBook = googleApi.searchByIsbn(isbn)
        Timber.d("Google book: $googleBook")
        var result = googleBook?.toBookFormData()
        if (result != null && result.isComplete()) {
            Timber.d("GoogleApi result is complete.")
            return result
        }

        // Try to get book from OpenLibrary API
        val openLibraryBook = openLibraryApi.searchByBibKeyIsbn(isbn)
        Timber.d("OpenLibrary book: $openLibraryBook")
        val openLibraryBookFormData = openLibraryBook?.toBookFormData()
        result = result?.updateNullValues(openLibraryBookFormData) ?: openLibraryBookFormData

        if (result != null && result.isComplete()) {
            Timber.d("OpenLibraryApi result is complete.")
            return result
        }

        // Try to get book from Goodreads API
        val goodreadsBook = goodreadsApi.findBookByIsbnOrNull(isbn)
        Timber.d("Goodreads book: $goodreadsBook")
        val goodreadsBookFormData = goodreadsBook?.toBookFormData()
        result = result?.updateNullValues(goodreadsBookFormData) ?: goodreadsBookFormData

        if (result != null && result.isComplete()) {
            Timber.d("GoodreadsApi result is complete.")
        } else {
            Timber.d("Api result is incomplete: $result")
        }

        // TODO: Additional search in other APIs (Goodreads reviews could fill some missing data)
        // Returns book data or null if no book was found
        return result
    }

    override suspend fun findBookCoverLinkForIsbn(isbn: String): String? {
        // Try to get book cover from OpenLibrary API
        val openLibraryBook = openLibraryApi.searchByBibKeyIsbn(isbn)
        Timber.d("OpenLibrary book: $openLibraryBook")
        if (openLibraryBook?.cover?.medium != null) {
            return openLibraryBook.cover.medium
        }
        // Try to get book cover from Google API
        val googleBook = googleApi.searchByIsbn(isbn)
        Timber.d("Google book: $googleBook")
        if (googleBook?.volumeInfo?.thumbnailUrl() != null) {
            return googleBook.volumeInfo.thumbnailUrl()
        }

        // Try to get book cover from Goodreads API
        val goodreadsBook = goodreadsApi.findBookByIsbnOrNull(isbn)
        Timber.d("Goodreads book: $goodreadsBook")
        if (goodreadsBook?.thumbnailUrl() != null) {
            return goodreadsBook.thumbnailUrl()
        }

        return null
    }

    override suspend fun findReviewsForIsbn(isbn: String?, title: String?, author: String?): GoodreadsBook? {
        return goodreadsApi.findReviewsOrNull(isbn = isbn, title = title, author = author)
    }

    override suspend fun findBookForQueryOnline(query: String): List<BookFormData> {
        val googleBooksSearchResponse = googleApi.searchByQuery(query)
        val googleBookFormDataList = googleBooksSearchResponse.take(10).map { it.toBookFormData() }
        val result = mutableListOf<BookFormData>()
        result.addAll(googleBookFormDataList)

        val openLibrarySearchResponse = openLibraryApi.searchByQuery(query)
        val openLibraryBookFormDataList: Collection<BookFormData> =
            openLibrarySearchResponse?.docs?.take(10)?.map { it.toBookFormData() } ?: emptyList()
        result.addAll(openLibraryBookFormDataList)

        return result
    }
}
