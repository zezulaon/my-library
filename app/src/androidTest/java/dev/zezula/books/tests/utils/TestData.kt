package dev.zezula.books.tests.utils

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.core.model.previewBooks
import dev.zezula.books.core.model.previewShelves

internal val testBooksData: List<Book> = previewBooks
internal val testShelvesData: List<Shelf> = previewShelves

internal val List<Book>.bookHobit: Book
    get() = first { it.title.equals("Hobit", ignoreCase = true) }

internal val List<Shelf>.shelfFavorites: Shelf
    get() = first { it.title.equals("My Favorites", ignoreCase = true) }

internal val List<Shelf>.shelfWishList: Shelf
    get() = first { it.title.equals("Wish List", ignoreCase = true) }
