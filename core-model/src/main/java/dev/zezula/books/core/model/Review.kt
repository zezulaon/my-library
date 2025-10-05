package dev.zezula.books.core.model

data class Review(
    val id: Id,
    val body: String? = null,
    val link: String? = null,
    val rating: Int? = null,
    val votes: Int? = null,
    val spoilerFlag: Boolean = false,
    val userName: String? = null,
    val userImageLink: String? = null,
) {

    @JvmInline
    value class Id(val value: String)
}

val previewReviews = listOf(
    Review(
        id = Review.Id("1"),
        body = "Test review body 1",
        link = null,
        rating = 4,
        votes = null,
        spoilerFlag = false,
        userName = "John",
        userImageLink = null,
    ),
    Review(
        id = Review.Id("2"),
        body = "Test review body 2",
        link = null,
        rating = 0,
        votes = null,
        spoilerFlag = false,
        userName = "Peter",
        userImageLink = null,
    ),
)
