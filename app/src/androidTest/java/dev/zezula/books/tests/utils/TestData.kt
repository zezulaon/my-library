package dev.zezula.books.tests.utils

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.core.model.previewShelves

data class TestBook(
    val id: Book.Id,
    val title: String,
    val author: String,
    val description: String,
    val isbn: String,
    val publisher: String,
    val yearPublished: Int,
    val pageCount: Int,
)

internal val testBooksData: List<TestBook> = listOf(
    TestBook(
        id = Book.Id("1"),
        title = "Hobit",
        author = "J. R. R. Tolkien",
        description = "Hobit description",
        isbn = "987789555",
        publisher = "Publisher 1",
        yearPublished = 2001,
        pageCount = 152,
    ),
    TestBook(
        id = Book.Id("2"),
        title = "Neverwhere",
        author = "N. Gaiman",
        description = "Neverwhere description",
        isbn = "987789554",
        publisher = "Publisher 2",
        yearPublished = 2001,
        pageCount = 152,
    ),
)

internal val testShelvesData: List<Shelf> = previewShelves

internal val List<TestBook>.bookHobit: TestBook
    get() = first { it.title.equals("Hobit", ignoreCase = true) }

internal val List<Shelf>.shelfFavorites: Shelf
    get() = first { it.title.equals("My Favorites", ignoreCase = true) }

internal val List<Shelf>.shelfWishList: Shelf
    get() = first { it.title.equals("Wish List", ignoreCase = true) }
