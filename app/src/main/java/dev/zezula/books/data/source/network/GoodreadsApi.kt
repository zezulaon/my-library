package dev.zezula.books.data.source.network

import dev.zezula.books.BuildConfig
import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.model.goodreads.GoodreadsResponse
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber

// https://www.goodreads.com/api/index#book.show_by_isbn (Unfortunately this API isn't maintained anymore)

interface GoodreadsApi {

    @GET("/book/isbn/{isbn}?text_only=true&key=" + BuildConfig.ML_GOODREADS_API_KEY)
    suspend fun goodreadsBook(@Path("isbn") isbn: String): GoodreadsResponse
}

suspend fun GoodreadsApi.findBookOrNull(isbn: String): GoodreadsBook? {
    // Search in GoodReads online DB
    return try {
        goodreadsBook(isbn).book
    } catch (e: HttpException) {
        Timber.w(e, "Failed to find a book for isbn: $isbn")
        null
    }
}
