package dev.zezula.books.ui.screen.authors

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.domain.GetBooksForAuthorUseCase
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.ui.DestinationArgs
import dev.zezula.books.ui.whileSubscribedInActivity
import dev.zezula.books.util.splitToAuthors
import dev.zezula.books.util.toAuthorNameId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class AuthorBooksViewModel(
    getBooksForAuthorUseCase: GetBooksForAuthorUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val authorNameId: String = checkNotNull(savedStateHandle[DestinationArgs.authorNameIdArg])

    private val errorMessage = MutableStateFlow<Int?>(null)

    private val booksResponse: Flow<Response<List<Book>>> = getBooksForAuthorUseCase(authorNameId)
        .onResponseError { errorMessage.value = R.string.error_failed_get_data }

    val uiState: StateFlow<AuthorBooksUiState> =
        combine(errorMessage, booksResponse) { errorMsg, booksResponse ->
            val books = booksResponse.getOrDefault(emptyList())
            val authorName = findFirstAuthorName(books)
            AuthorBooksUiState(
                errorMessage = errorMsg,
                books = books,
                authorName = authorName,
            )
        }
            .stateIn(
                scope = this.viewModelScope,
                started = whileSubscribedInActivity,
                initialValue = AuthorBooksUiState(),
            )

    /**
     * Finds first author name that matches the [authorNameId] in the list of books.
     */
    private fun findFirstAuthorName(books: List<Book>): String? {
        val allAuthors = books.flatMap { it.author?.splitToAuthors() ?: emptyList() }
        return allAuthors.firstOrNull { it.toAuthorNameId() == authorNameId }
    }

    init {
        Timber.d("init{}")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }
}
