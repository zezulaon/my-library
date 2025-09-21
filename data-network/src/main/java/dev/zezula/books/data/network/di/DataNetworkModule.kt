package dev.zezula.books.data.network.di

import dev.zezula.books.data.network.api.FirestoreDataSource
import dev.zezula.books.data.network.api.GoodreadsApi
import dev.zezula.books.data.network.api.GoogleApi
import dev.zezula.books.data.network.api.MyLibraryApi
import dev.zezula.books.data.network.api.NetworkDataSource
import dev.zezula.books.data.network.api.OpenLibraryApi
import dev.zezula.books.data.network.fake.FakeNetworkDataSourceImpl
import dev.zezula.data.network.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
        // SimpleXmlConverterFactory is deprecated but working. There seems to be no alternative for Android right now:
        // https://github.com/square/retrofit/issues/2733
        @Suppress("DEPRECATION")
        val create = retrofit2.converter.simplexml.SimpleXmlConverterFactory.create()

        Retrofit.Builder()
            .addConverterFactory(create)
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
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.googleapis.com/")
            .build()
            .create(GoogleApi::class.java)
    }
    single<MyLibraryApi> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.ML_BASE_API_URL)
            .client(get())
            .build()
            .create(MyLibraryApi::class.java)
    }

    single<NetworkDataSource> {
        FirestoreDataSource()
    }
}

val testDataNetworkModuleOverride = module {
    factory<NetworkDataSource> { FakeNetworkDataSourceImpl() }
}