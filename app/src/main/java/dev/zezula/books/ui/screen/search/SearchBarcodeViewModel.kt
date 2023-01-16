package dev.zezula.books.ui.screen.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.domain.FindBookOnlineUseCase
import dev.zezula.books.ui.DestinationArgs
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchBarcodeViewModel(
    private val findBookOnlineUseCase: FindBookOnlineUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val barcode: String = checkNotNull(savedStateHandle[DestinationArgs.barcodeArg])

    private val _errorMessage = MutableStateFlow<Int?>(null)
    private val _isInProgress = MutableStateFlow(false)
    private val _noBookFound = MutableStateFlow(false)
    private val _foundBookId = MutableStateFlow<String?>(null)

    val uiState = combine(
        _isInProgress,
        _errorMessage,
        _noBookFound,
        _foundBookId,
    ) { isInProgress, errorMessage, noBookFound, foundBookId ->
        SearchBarcodeUiState(
            barcode = barcode,
            isInProgress = isInProgress,
            errorMessage = errorMessage,
            noBookFound = noBookFound,
            foundBookId = foundBookId,
        )
    }
        .stateIn(viewModelScope, whileSubscribedInActivity, SearchBarcodeUiState())

    init {
        Timber.d("init{}")
        Timber.d("Received barcode: $barcode")

        searchBook(barcode)
    }

    private fun searchBook(barcode: String) {
        viewModelScope.launch {
            _isInProgress.value = true
            findBookOnlineUseCase(barcode)
                .fold(
                    onSuccess = { bookId ->
                        if (bookId != null) {
                            _foundBookId.value = bookId
                        } else {
                            _noBookFound.value = true
                        }
                    },
                    onFailure = {
                        _errorMessage.value = R.string.error_failed_get_data
                    }
                )
            _isInProgress.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }
}