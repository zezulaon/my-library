package dev.zezula.books.ui.screen.detail

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Note
import dev.zezula.books.core.model.Rating
import dev.zezula.books.core.model.Review
import dev.zezula.books.core.model.ShelfForBook

data class BookDetailUiState(
    val book: Book? = null,
    val isBookInLibrary: Boolean = false,
    val rating: Rating? = null,
    val shelves: List<ShelfForBook> = emptyList(),
    val notes: List<Note> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val suggestionsUiState: SuggestionsUiState = SuggestionsUiState(),
    val selectedTab: DetailTab = DetailTab.Detail,
    val errorMessage: Int? = null,
    val isBookDeleted: Boolean = false,
    val isReviewsSearchInProgress: Boolean = false,
    val isDeleteDialogDisplayed: Boolean = false,
    val isNewNoteDialogDisplayed: Boolean = false,
    val selectedNote: Note? = null,
)

data class SuggestionsUiState(
    val suggestions: List<Book> = emptyList(),
    val isGeneratingInProgress: Boolean = false,
    val refreshFailed: Boolean = false,
)
