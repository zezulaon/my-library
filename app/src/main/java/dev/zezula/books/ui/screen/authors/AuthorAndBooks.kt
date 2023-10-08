package dev.zezula.books.ui.screen.authors

/**
 * Represents a single author with the number of their books. Used in the [AllAuthorsViewModel].
 */
data class AuthorAndBooks(
    val authorNameId: String,
    val authorName: String,
    var numberOfBooks: Int,
)
