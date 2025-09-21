package dev.zezula.data.network

import dev.zezula.books.core.utils.test.TimberLogRule
import dev.zezula.books.data.network.api.GoodreadsApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response

class GoodReads {

    companion object {

        @get:ClassRule
        @JvmStatic // JvmStatic to satisfy ClassRule field requirements
        val timberLogRule = TimberLogRule()
    }

    private lateinit var goodreadsApi: GoodreadsApi

    @Before
    fun setupRepository() = runTest {
        goodreadsApi = Mockito.mock()
        whenever(goodreadsApi.goodreadsBookWithReviews(any())).thenAnswer {
            throw HttpException(Response.error<String>(400, ResponseBody.create(null, "Error")))
        }
    }

    @Test(expected = HttpException::class)
    fun mocked_api_throws_httpException() = runTest {
        goodreadsApi.goodreadsBookWithReviews("yyy")
    }
}