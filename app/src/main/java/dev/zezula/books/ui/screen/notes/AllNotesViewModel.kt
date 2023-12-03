package dev.zezula.books.ui.screen.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.note.NoteWithBook
import dev.zezula.books.domain.GetAllNotesUseCase
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

class AllNotesViewModel(
    getAllNotesUseCase: GetAllNotesUseCase,
) : ViewModel() {

    private val errorMessage = MutableStateFlow<Int?>(null)

    private val notes: Flow<Response<List<NoteWithBook>>> = getAllNotesUseCase()
        .onResponseError { errorMessage.value = R.string.error_failed_get_data }

    val uiState: StateFlow<AllNotesUiState> =
        combine(errorMessage, notes) { errorMsg, notes ->
            AllNotesUiState(
                errorMessage = errorMsg,
                notes = notes.getOrDefault(emptyList()),
            )
        }
            .stateIn(
                scope = this.viewModelScope,
                started = whileSubscribedInActivity,
                initialValue = AllNotesUiState(),
            )

    init {
        Timber.d("init{}")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }
}
