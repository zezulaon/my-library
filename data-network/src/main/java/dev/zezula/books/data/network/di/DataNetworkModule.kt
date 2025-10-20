package dev.zezula.books.data.network.di

import dev.zezula.books.core.utils.di.GOODREADS_API_KEY_QUALIFIER
import dev.zezula.books.core.utils.di.GOOGLE_API_KEY_QUALIFIER
import dev.zezula.books.data.network.api.FirestoreDataSource
import dev.zezula.books.data.network.api.GoodreadsApi
import dev.zezula.books.data.network.api.GoogleApi
import dev.zezula.books.data.network.api.NetworkDataSource
import dev.zezula.books.data.network.api.OpenLibraryApi
import dev.zezula.books.data.network.fake.FakeNetworkDataSourceImpl
import dev.zezula.data.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val dataNetworkModule = module {

    factory<OkHttpClient> {
        val clientBuilder = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(get<HttpLoggingInterceptor>())
        }
        clientBuilder.build()
    }

    factory<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    // Network services
    single<GoodreadsApi> {
        val apiKey: String = get(named(GOODREADS_API_KEY_QUALIFIER))

        // SimpleXmlConverterFactory is deprecated but working. There seems to be no alternative for Android right now:
        // https://github.com/square/retrofit/issues/2733
        @Suppress("DEPRECATION")
        val create = retrofit2.converter.simplexml.SimpleXmlConverterFactory.create()

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.response(host = "www.goodreads.com", queryParameter = "key", apiKey = apiKey)
            }
            .build()

        Retrofit.Builder()
            .addConverterFactory(create)
            .client(client)
            .baseUrl("https://www.goodreads.com/")
            .build()
            .create(GoodreadsApi::class.java)
    }
    single<OpenLibraryApi> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://openlibrary.org/")
            .client(get())
            .build()
            .create(OpenLibraryApi::class.java)
    }
    single<GoogleApi> {
        val apiKey: String = get(named(GOOGLE_API_KEY_QUALIFIER))

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.response(host = "www.googleapis.com", queryParameter = "key", apiKey = apiKey)
            }
            .build()

        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl("https://www.googleapis.com/")
            .build()
            .create(GoogleApi::class.java)
    }

    single<NetworkDataSource> {
        FirestoreDataSource()
    }
}

private fun Interceptor.Chain.response(host: String, queryParameter: String, apiKey: String): Response {
    val req = request()
    val url = req.url

    val newUrl = if (url.host == host && url.queryParameter(queryParameter) == null) {
        url.newBuilder().addQueryParameter(queryParameter, apiKey).build()
    } else {
        url
    }

    return proceed(req.newBuilder().url(newUrl).build())
}

val testDataNetworkModuleOverride = module {
    factory<NetworkDataSource> { FakeNetworkDataSourceImpl() }
}
