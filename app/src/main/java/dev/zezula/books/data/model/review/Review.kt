package dev.zezula.books.data.model.review

data class Review(
    val id: String,
    val body: String? = null,
    val link: String? = null,
    val rating: Int? = null,
    val votes: Int? = null,
    val spoilerFlag: Boolean = false,
    val userName: String? = null,
    val userImageLink: String? = null,
)

val previewReviews = listOf(
    Review(
        id = "1",
        body = "Test review body 1",
        link = null,
        rating = 4,
        votes = null,
        spoilerFlag = false,
        userName = "John",
        userImageLink = null,
    ),
    Review(
        id = "2",
        body = "Test review body 2",
        link = null,
        rating = 0,
        votes = null,
        spoilerFlag = false,
        userName = "Peter",
        userImageLink = null,
    ),
)
