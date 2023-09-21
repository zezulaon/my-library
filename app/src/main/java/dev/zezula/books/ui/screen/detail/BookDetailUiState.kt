package dev.zezula.books.ui.screen.detail

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.review.Rating
import dev.zezula.books.data.model.review.Review
import dev.zezula.books.data.model.shelf.ShelfForBook

data class BookDetailUiState(
    val book: Book? = null,
    val rating: Rating? = null,
    val shelves: List<ShelfForBook> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val selectedTab: DetailTab = DetailTab.Detail,
    val errorMessage: Int? = null,
    val isBookDeleted: Boolean = false,
    val isInProgress: Boolean = false,
    val isDeleteDialogDisplayed: Boolean = false,
)
