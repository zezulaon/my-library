package dev.zezula.books.ui.screen.authors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.domain.GetAllAuthorsUseCase
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class AllAuthorsViewModel(
    getAllAuthorsUseCase: GetAllAuthorsUseCase,
) : ViewModel() {

    private val errorMessage = MutableStateFlow<Int?>(null)

    private val authors: Flow<Response<List<AuthorAndBooks>>> = getAllAuthorsUseCase()
        .onResponseError { errorMessage.value = R.string.error_failed_get_data }

    val uiState: StateFlow<AllAuthorsUiState> =
        combine(errorMessage, authors) { errorMsg, authors ->
            AllAuthorsUiState(
                errorMessage = errorMsg,
                authors = authors.getOrDefault(emptyList()),
            )
        }
            .stateIn(
                scope = this.viewModelScope,
                started = whileSubscribedInActivity,
                initialValue = AllAuthorsUiState(),
            )

    init {
        Timber.d("init{}")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }
}
