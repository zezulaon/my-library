package dev.zezula.books.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.domain.AddOrUpdateBookUseCase
import dev.zezula.books.domain.FindBookForQueryOnlineUseCase
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

data class BookFormWithId(val bookFormData: BookFormData, val bookId: String? = null)

class FindBookViewModel(
    private val addOrUpdateBookUseCase: AddOrUpdateBookUseCase,
    private val findBookForQueryOnlineUseCase: FindBookForQueryOnlineUseCase,
) : ViewModel() {

    private val _searchResultBooks = MutableStateFlow<List<BookFormWithId>>(emptyList())
    private val _errorMessage = MutableStateFlow<Int?>(null)
    private val _isInProgress = MutableStateFlow(false)
    private val _noResultsMsgDisplayed = MutableStateFlow(false)

    val uiState = combine(
        _isInProgress,
        _errorMessage,
        _searchResultBooks,
        _noResultsMsgDisplayed,
    ) { isInProgress, errorMessage, searchResultBooks, noResultMsgDisplayed ->
        FindBookUiState(
            isInProgress = isInProgress,
            noResultsMsgDisplayed = noResultMsgDisplayed,
            errorMessage = errorMessage,
            foundBooks = searchResultBooks,
        )
    }
        .stateIn(viewModelScope, whileSubscribedInActivity, FindBookUiState())

    init {
        Timber.d("init{}")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _isInProgress.value = true
            _searchResultBooks.value = emptyList()
            _noResultsMsgDisplayed.value = false
            findBookForQueryOnlineUseCase(query)
                .fold(
                    onSuccess = { books ->
                        if (books.isNotEmpty()) {
                            Timber.d("Found books: $books")
                            _searchResultBooks.value = books.map { BookFormWithId(it) }
                        } else {
                            Timber.d("No books found")
                            _noResultsMsgDisplayed.value = true
                        }
                    },
                    onFailure = {
                        _errorMessage.value = R.string.error_failed_get_data
                    },
                )
            _isInProgress.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun addBook(bookFormIndex: Int) {
        viewModelScope.launch {
            val bookFormData = _searchResultBooks.value[bookFormIndex].bookFormData
            addOrUpdateBookUseCase(null, bookFormData)
                .fold(
                    onSuccess = { book ->
                        Timber.d("Book added: $book")
                        _searchResultBooks.value = _searchResultBooks.value.mapIndexed { i, bookFormWithId ->
                            if (i == bookFormIndex) {
                                BookFormWithId(bookFormWithId.bookFormData, book.id)
                            } else {
                                bookFormWithId
                            }
                        }
                    },
                    onFailure = {
                        Timber.e(it, "Failed to add book.")
                    },
                )
        }
    }
}
