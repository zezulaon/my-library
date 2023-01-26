package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.review.ReviewEntity
import dev.zezula.books.data.source.db.ReviewDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeReviewDaoImpl : ReviewDao {

    private var reviewFlow: MutableStateFlow<List<ReviewEntity>> = MutableStateFlow(emptyList())

    override fun getReviews(bookId: String): Flow<List<ReviewEntity>> {
        return reviewFlow
    }

    override suspend fun addReviews(reviews: List<ReviewEntity>) {
        reviewFlow.value = reviews
    }

    override suspend fun deleteAllForBookId(bookId: String) {
        reviewFlow.value = emptyList()
    }
}
