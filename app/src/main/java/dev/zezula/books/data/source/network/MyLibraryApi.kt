package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.myLibrary.MyLibraryBook
import retrofit2.http.GET
import retrofit2.http.Query

interface MyLibraryApi {

    @GET("books/v1/suggestions")
    suspend fun suggestions(
        @Query("title") title: String,
        @Query("author") author: String,
        @Query("isbn") isbn: String? = null,
    ): List<MyLibraryBook>?
}
