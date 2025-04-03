package dev.zezula.books.data.model.review

import dev.zezula.books.data.model.book.Book
import java.text.NumberFormat
import java.util.Locale

data class Rating(
    val id: String,
    val bookId: Book.Id,
    val averageRating: String? = null,
    val textReviewsCount: Int? = null,
    val ratingsCount: Int? = null,
) {
    val textReviewsCountFormatted: String =
        NumberFormat.getInstance(Locale.getDefault()).format(textReviewsCount?.toLong() ?: 0L)

    val ratingsCountFormatted: String =
        NumberFormat.getInstance(Locale.getDefault()).format(ratingsCount?.toLong() ?: 0L)
}

val previewRatings = listOf(
    Rating(id = "1", bookId = Book.Id("1"), averageRating = "3.5", textReviewsCount = 120, ratingsCount = 50),
    Rating(id = "2", bookId = Book.Id("2"), averageRating = "3.5", textReviewsCount = 120, ratingsCount = 50),
)
