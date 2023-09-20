package dev.zezula.books.data.source.network

import dev.zezula.books.BuildConfig
import dev.zezula.books.data.model.google.GoogleSearchItem
import dev.zezula.books.data.model.google.GoogleSearchResponse
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber

// https://developers.google.com/books/docs/v1/using
interface GoogleApi {

    @GET("/books/v1/volumes?key=" + BuildConfig.ML_GOOGLE_API_KEY)
    suspend fun searchByQuery(@Query("q") query: String): GoogleSearchResponse?
}

suspend fun GoogleApi.searchByIsbn(isbn: String): GoogleSearchItem? {
    return try {
        searchByQuery("isbn:$isbn")?.items?.firstOrNull { searchItem ->
            // Check if the isbn is in the list of identifiers
            val isbnItems = searchItem.volumeInfo?.industryIdentifiers?.mapNotNull { it.identifier }
            isbnItems?.contains(isbn) ?: false
        }
    } catch (e: HttpException) {
        Timber.w(e, "Failed to find book for isbn: $isbn")
        null
    }
}
