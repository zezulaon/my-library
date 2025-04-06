package dev.zezula.books.data

import dev.zezula.books.data.model.FindBookOnlineResponse
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.review.Rating
import dev.zezula.books.data.model.review.Review
import kotlinx.coroutines.flow.Flow

interface ReviewsRepository {

    fun getReviewsForBookFlow(bookId: Book.Id): Flow<List<Review>>

    fun getRatingForBookFlow(bookId: Book.Id): Flow<Rating?>

    suspend fun refreshReviews(book: Book)

    suspend fun addReviews(book: Book, fetchBookNetworkResponse: FindBookOnlineResponse)
}
