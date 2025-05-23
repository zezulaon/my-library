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
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.RatingDao
import dev.zezula.books.data.source.db.ReviewDao
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.util.removeHtmlTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import java.util.UUID

class ReviewsRepositoryImpl(
    private val reviewsDao: ReviewDao,
    private val ratingsDao: RatingDao,
    private val bookDao: BookDao,
    private val onlineBookFinderService: OnlineBookFinderService,
) : ReviewsRepository {

    override fun getReviewsForBookFlow(bookId: Book.Id): Flow<List<Review>> {
        return reviewsDao.getReviewsForBookFlow(bookId).mapNotNull {
            it.map(ReviewEntity::asExternalModel)
        }
    }

    override fun getRatingForBookFlow(bookId: Book.Id): Flow<Rating?> {
        return ratingsDao.getRatingForBookFlow(bookId)
            .map {
                it?.asExternalModel()
            }
    }

    override suspend fun refreshReviews(book: Book) {
        val goodreadsBook = onlineBookFinderService.findReviewsForIsbn(
            isbn = book.isbn,
            title = book.title,
            author = book.author,
        )
        if (goodreadsBook != null) {
            addReviews(book, FindBookOnlineResponse(goodreadsBook = goodreadsBook))
        }
    }

    override suspend fun addReviews(
        book: Book,
        fetchBookNetworkResponse: FindBookOnlineResponse,
    ) {
        val existingBook = bookDao.getBookFlow(book.id).first()
        // Check if there is a book in the database
        if (existingBook == null) {
            Timber.w("Cannot save reviews -> Book with id: [${book.id}] not found in database.")
            return
        }

        val goodreadsBook = fetchBookNetworkResponse.goodreadsBook
        if (goodreadsBook?.reviews != null) {
            insertRating(book.id, goodreadsBook)
        }

        val goodreadsReviews = fetchBookNetworkResponse.goodreadsBook?.reviews
        if (goodreadsReviews != null) {
            insertReviews(book.id, goodreadsReviews)
        }
    }

    private suspend fun insertReviews(bookId: Book.Id, goodReadsReview: List<GoodReadsReview>) {
        val reviews = goodReadsReview
            .map {
                ReviewEntity(
                    id = Review.Id(UUID.randomUUID().toString()),
                    bookId = bookId,
                    body = it.body?.removeHtmlTags()?.trim(),
                    link = it.link,
                    rating = it.rating,
                    votes = it.votes,
                    spoilerFlag = it.spoiler_flag ?: false,
                    userName = it.user?.name,
                    userImageLink = it.user?.image_url,
                )
            }
        reviewsDao.deleteAllForBookId(bookId)
        reviewsDao.addReviews(reviews)
    }

    private suspend fun insertRating(bookId: Book.Id, goodreadsBook: GoodreadsBook) {
        val rating = with(goodreadsBook) {
            RatingEntity(
                id = Rating.Id(UUID.randomUUID().toString()),
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
