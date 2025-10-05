package dev.zezula.books.core.model

/**
 * Represents a single author with the number of their books.
 */
data class AuthorAndBooks(
    val authorNameId: String,
    val authorName: String,
    var numberOfBooks: Int,
)
