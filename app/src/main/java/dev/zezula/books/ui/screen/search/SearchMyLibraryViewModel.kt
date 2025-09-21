package dev.zezula.books.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.core.model.Book
import dev.zezula.books.domain.usecases.SearchMyLibraryBooksUseCase
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchMyLibraryViewModel(
    private val searchMyLibraryBooksUseCase: SearchMyLibraryBooksUseCase,
) : ViewModel() {

    private val _currentSearchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<Book>>(emptyList())

    val uiState = combine(
        _currentSearchQuery,
        _searchResults,
    ) { currentSearchQuery, searchResults ->
        SearchMyLibraryUiState(
            currentSearchQuery = currentSearchQuery,
            searchResults = searchResults,
        )
    }
        .stateIn(viewModelScope, whileSubscribedInActivity, SearchMyLibraryUiState())

    init {
        Timber.d("init{}")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun onSearchQueryChanged(query: String) {
        _currentSearchQuery.value = query

        if (query.length < 2) {
            Timber.d("Query is too short: [$query]")
            _searchResults.value = emptyList()
        } else {
            searchBooks(query)
        }
    }

    private fun searchBooks(query: String) {
        viewModelScope.launch {
            _searchResults.value = emptyList()
            searchMyLibraryBooksUseCase(query)
                .fold(
                    onSuccess = { books ->
                        if (books.isNotEmpty()) {
                            Timber.d("Found ${books.size}")
                            _searchResults.value = books
                        } else {
                            Timber.d("No books found")
                        }
                    },
                    onFailure = {
                        Timber.d("Failed to search book for query: [$query].")
                    },
                )
        }
    }
}
