package dev.zezula.books.data.model.myLibrary

import dev.zezula.books.data.model.book.BookFormData

data class MyLibraryBook(
    val title: String,
    val author: String? = null,
    val description: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val yearPublished: Int? = null,
    val pageCount: Int? = null,
    val thumbnailLink: String? = null,
)

fun MyLibraryBook.toBookFormData(): BookFormData {
    return BookFormData(
        title = title,
        author = author,
        description = description,
        isbn = isbn,
        publisher = publisher,
        yearPublished = yearPublished,
        pageCount = pageCount,
        thumbnailLink = thumbnailLink,
    )
}
