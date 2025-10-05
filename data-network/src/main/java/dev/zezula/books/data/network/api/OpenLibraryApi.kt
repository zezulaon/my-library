package dev.zezula.books.data.network.api

import dev.zezula.books.data.network.dto.openLibrary.OpenLibraryBibKeyItem
import dev.zezula.books.data.network.dto.openLibrary.OpenLibraryIsbnResponse
import dev.zezula.books.data.network.dto.openLibrary.OpenLibrarySearchResponse
import dev.zezula.books.data.network.dto.openLibrary.containsIsbn
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// https://openlibrary.org/developers/api
interface OpenLibraryApi {

    @GET("/search.json")
    suspend fun searchByQuery(
        @Query("q") query: String? = null,
        @Query("author") author: String? = null,
        @Query("title") title: String? = null,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int = 20,
    ): OpenLibrarySearchResponse?

    @GET("/isbn/{isbn}.json")
    suspend fun searchByIsbn(@Path("isbn") isbn: String): OpenLibraryIsbnResponse?

    // https://openlibrary.org/api/books?bibkeys=ISBN:9781401208417&jscmd=data&format=json
    @GET("/api/books?jscmd=data&format=json")
    suspend fun searchByBibKey(@Query("bibkeys") key: String): Map<String, OpenLibraryBibKeyItem>?
}

suspend fun OpenLibraryApi.searchByBibKeyIsbn(key: String): OpenLibraryBibKeyItem? {
    return searchByBibKey("ISBN:$key")?.values?.firstOrNull { bibKeyItem ->
        // Check if the isbn is in the list of identifiers
        bibKeyItem.containsIsbn(key)
    }
}
