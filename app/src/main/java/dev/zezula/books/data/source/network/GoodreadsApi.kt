package dev.zezula.books.data.source.network

import dev.zezula.books.BuildConfig
import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.model.goodreads.GoodreadsResponse
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import timber.log.Timber

// https://www.goodreads.com/api/index#book.show_by_isbn (Unfortunately this API isn't maintained anymore)
interface GoodreadsApi {

    @GET("/book/isbn/{isbn}?key=" + BuildConfig.ML_GOODREADS_API_KEY)
    suspend fun goodreadsBookWithReviews(@Path("isbn") isbn: String): GoodreadsResponse

    @GET("/search/index.xml?key=" + BuildConfig.ML_GOODREADS_API_KEY)
    suspend fun searchBookByQuery(@Query("q") query: String): GoodreadsResponse

    @GET("/book/title.xml?key=" + BuildConfig.ML_GOODREADS_API_KEY)
    suspend fun goodreadsBookByTitle(@Query("title") title: String): GoodreadsResponse
}

suspend fun GoodreadsApi.findReviewsOrNull(isbn: String): GoodreadsBook? {
    // Search in GoodReads online DB
    return try {
        goodreadsBookWithReviews(isbn).book
    } catch (e: HttpException) {
        Timber.w(e, "Failed to find reviews for isbn: $isbn")
        null
    }
}

suspend fun GoodreadsApi.findBookByIsbnOrNull(isbn: String): GoodreadsBook? {
    // Search in GoodReads online DB
    return try {
        searchBookByQuery(isbn).search?.results?.firstOrNull()?.best_book?.apply {
            // search/index.xml doesn't return isbn, so we need to add it manually.
            this.isbn = isbn
        }
    } catch (e: HttpException) {
        Timber.w(e, "Failed to find a book for isbn: $isbn")
        null
    }
}
