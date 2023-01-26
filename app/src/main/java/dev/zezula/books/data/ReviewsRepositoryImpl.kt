package dev.zezula.books.data

import dev.zezula.books.data.model.FindBookOnlineResponse
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.goodreads.GoodReadsReview
import dev.zezula.books.data.model.goodreads.GoodreadsBook
import dev.zezula.books.data.model.review.Rating
import dev.zezula.books.data.model.review.RatingEntity
import dev.zezula.books.data.model.review.Review
import dev.zezula.books.data.model.review.ReviewEntity
import dev.zezula.books.data.model.review.asExternalModel
import dev.zezula.books.data.source.db.RatingDao
import dev.zezula.books.data.source.db.ReviewDao
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.util.removeHtmlTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.util.UUID

class ReviewsRepositoryImpl(
    private val reviewsDao: ReviewDao,
    private val ratingsDao: RatingDao,
    private val onlineBookFinderService: OnlineBookFinderService,
) : ReviewsRepository {

    override fun getReviewsForBookAsStream(bookId: String): Flow<List<Review>> {
        return reviewsDao.getReviews(bookId).mapNotNull {
            it.map(ReviewEntity::asExternalModel)
        }
    }

    override fun getRatingStream(bookId: String): Flow<Rating?> {
        return ratingsDao.getRating(bookId)
            .map {
                it?.asExternalModel()
            }
    }

    override suspend fun refreshReviews(book: Book) {
        if (book.isbn != null && book.isbn.isNotEmpty()) {
            val goodreadsBook = onlineBookFinderService.findBookOnline(book.isbn).goodreadsBook
            if (goodreadsBook != null) {
                addReviews(book, FindBookOnlineResponse(goodreadsBook = goodreadsBook))
            }
        }
    }

    override suspend fun addReviews(
        book: Book,
        fetchBookNetworkResponse: FindBookOnlineResponse
    ) {
        val goodreadsBook = fetchBookNetworkResponse.goodreadsBook
        if (goodreadsBook?.reviews != null) {
            insertRating(book.id, goodreadsBook)
        }

        val goodreadsReviews = fetchBookNetworkResponse.goodreadsBook?.reviews
        if (goodreadsReviews != null) {
            insertReviews(book.id, goodreadsReviews)
        }
    }

    private suspend fun insertReviews(bookId: String, goodReadsReview: List<GoodReadsReview>) {
        val reviews = goodReadsReview
            .map {
                ReviewEntity(
                    id = UUID.randomUUID().toString(),
                    bookId = bookId,
                    body = it.body?.removeHtmlTags()?.trim(),
                    link = it.link,
                    rating = it.rating,
                    votes = it.votes,
                    spoilerFlag = it.spoiler_flag ?: false,
                    userName = it.user?.name,
                    userImageLink = it.user?.image_url
                )
            }
        reviewsDao.deleteAllForBookId(bookId)
        reviewsDao.addReviews(reviews)
    }

    private suspend fun insertRating(bookId: String, goodreadsBook: GoodreadsBook) {
        val rating = with(goodreadsBook) {
            RatingEntity(
                id = UUID.randomUUID().toString(),
                bookId = bookId,
                averageRating = this.average_rating,
                textReviewsCount = this.work?.text_reviews_count,
                ratingsCount = this.work?.ratings_count,
            )
        }
        ratingsDao.deleteAllForBookId(bookId)
        ratingsDao.addRating(rating)
    }
}