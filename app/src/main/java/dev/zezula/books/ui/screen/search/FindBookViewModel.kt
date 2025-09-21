package dev.zezula.books.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.core.model.Book
import dev.zezula.books.domain.usecases.FindBookForQueryOnlineUseCase
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class FindBookViewModel(
    private val findBookForQueryOnlineUseCase: FindBookForQueryOnlineUseCase,
) : ViewModel() {

    private val _searchResultBooks = MutableStateFlow<List<Book>>(emptyList())
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
                            _searchResultBooks.value = books
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
}
