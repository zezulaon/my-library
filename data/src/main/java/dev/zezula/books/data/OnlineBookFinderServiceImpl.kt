package dev.zezula.books.data

import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.core.model.isComplete
import dev.zezula.books.core.model.updateNullValues
import dev.zezula.books.data.network.api.GoodreadsApi
import dev.zezula.books.data.network.api.GoogleApi
import dev.zezula.books.data.network.api.OpenLibraryApi
import dev.zezula.books.data.network.api.findBookByIsbnOrNull
import dev.zezula.books.data.network.api.searchByBibKeyIsbn
import dev.zezula.books.data.network.api.searchByIsbn
import dev.zezula.books.data.network.api.searchByQuery
import dev.zezula.books.data.network.dto.goodreads.thumbnailUrl
import dev.zezula.books.data.network.dto.goodreads.toBookFormData
import dev.zezula.books.data.network.dto.google.thumbnailUrl
import dev.zezula.books.data.network.dto.google.toBookFormData
import dev.zezula.books.data.network.dto.openLibrary.toBookFormData
import dev.zezula.books.domain.services.OnlineBookFinderService
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
        val openLibraryCover = openLibraryBook?.cover?.medium
        if (openLibraryCover != null) {
            return openLibraryCover
        }
        // Try to get book cover from Google API
        val googleBook = googleApi.searchByIsbn(isbn)
        Timber.d("Google book: $googleBook")
        val googleCover = googleBook?.volumeInfo?.thumbnailUrl()
        if (googleCover != null) {
            return googleCover
        }

        // Try to get book cover from Goodreads API
        val goodreadsBook = goodreadsApi.findBookByIsbnOrNull(isbn)
        Timber.d("Goodreads book: $goodreadsBook")
        if (goodreadsBook?.thumbnailUrl() != null) {
            return goodreadsBook.thumbnailUrl()
        }

        return null
    }

    override suspend fun findBookForQueryOnline(query: String): List<BookFormData> {
        val googleBooksSearchResponse = googleApi.searchByQuery(query)
        val googleBookFormDataList = googleBooksSearchResponse.take(10).map { it.toBookFormData() }
        val result = mutableListOf<BookFormData>()
        result.addAll(googleBookFormDataList)

        val openLibrarySearchResponse = openLibraryApi.searchByQuery(query = query, limit = 20)
        val openLibraryBookFormDataList: Collection<BookFormData> =
            openLibrarySearchResponse?.docs?.take(10)?.map { it.toBookFormData() } ?: emptyList()
        result.addAll(openLibraryBookFormDataList)

        return result
    }
}
