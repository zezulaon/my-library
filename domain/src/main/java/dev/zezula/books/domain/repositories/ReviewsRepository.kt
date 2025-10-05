package dev.zezula.books.domain.repositories

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Rating
import dev.zezula.books.core.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewsRepository {

    fun getReviewsForBookFlow(bookId: Book.Id): Flow<List<Review>>

    fun getRatingForBookFlow(bookId: Book.Id): Flow<Rating?>

    suspend fun refreshReviews(book: Book)
}
