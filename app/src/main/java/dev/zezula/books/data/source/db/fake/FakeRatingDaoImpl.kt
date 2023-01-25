package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.review.RatingEntity
import dev.zezula.books.data.source.db.RatingDao
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRatingDaoImpl : RatingDao {

    private var ratingFlow: MutableStateFlow<RatingEntity?> = MutableStateFlow(null)

    override fun getRating(bookId: String) = ratingFlow

    override suspend fun addRating(rating: RatingEntity) {
        ratingFlow.value = rating
    }

    override suspend fun deleteAllForBookId(bookId: String) {
        ratingFlow.value = null
    }
}