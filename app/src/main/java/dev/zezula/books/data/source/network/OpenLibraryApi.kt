package dev.zezula.books.data.source.network

import dev.zezula.books.data.model.openLibrary.OpenLibraryIsbnResponse
import dev.zezula.books.data.model.openLibrary.OpenLibrarySearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// https://openlibrary.org/developers/api
interface OpenLibraryApi {

    @GET("/search.json?limit=20")
    suspend fun searchByQuery(@Query("q") query: String): OpenLibrarySearchResponse?

    @GET("/isbn/{isbn}.json")
    suspend fun searchByIsbn(@Path("isbn") isbn: String): OpenLibraryIsbnResponse?
}
