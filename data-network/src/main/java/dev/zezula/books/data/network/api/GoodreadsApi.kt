package dev.zezula.books.data.network.api

import dev.zezula.books.data.network.dto.goodreads.GoodreadsBook
import dev.zezula.books.data.network.dto.goodreads.GoodreadsResponse
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import timber.log.Timber

// https://www.goodreads.com/api/index#book.show_by_isbn (Unfortunately this API isn't maintained anymore)
interface GoodreadsApi {

    @GET("/book/isbn/{isbn}")
    suspend fun goodreadsBookWithReviews(@Path("isbn") isbn: String): GoodreadsResponse

    @GET("/book/title.xml")
    suspend fun goodreadsBookWithReviews(
        @Query("title") title: String,
        @Query("author") author: String,
    ): GoodreadsResponse

    @GET("/search/index.xml")
    suspend fun searchBookByQuery(@Query("q") query: String): GoodreadsResponse

    @GET("/book/title.xml")
    suspend fun goodreadsBookByTitle(@Query("title") title: String): GoodreadsResponse
}

suspend fun GoodreadsApi.findReviewsOrNull(isbn: String?, title: String?, author: String?): GoodreadsBook? {
    // Search in GoodReads online DB
    var result: GoodreadsBook? = null
    if (isbn != null) {
        result = try {
            goodreadsBookWithReviews(isbn).book
        } catch (e: HttpException) {
            Timber.w(e, "Failed to find reviews for isbn: $isbn")
            null
        }
    }

    if (result == null) {
        Timber.d("Trying to find reviews by title: $title and author: $author")
        if (title != null && author != null) {
            // Try to search for reviews by title and author
            result = goodreadsBookWithReviews(title, author).book
        }
    }

    return result
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
