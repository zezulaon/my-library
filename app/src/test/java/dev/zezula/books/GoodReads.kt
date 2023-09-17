package dev.zezula.books

import dev.zezula.books.data.source.network.GoodreadsApi
import dev.zezula.books.data.source.network.findReviewsOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class GoodReads {

    companion object {

        @get:ClassRule
        @JvmStatic // JvmStatic to satisfy ClassRule field requirements
        val timberLogRule = TimberLogRule()
    }

    private lateinit var goodreadsApi: GoodreadsApi

    @OptIn(ExperimentalCoroutinesApi::class)
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

    @Test
    fun find_books_is_mapped_to_null_when_goodreads_throws_httpException() = runTest {
        assertNull(goodreadsApi.findReviewsOrNull("xxx"))
    }
}
