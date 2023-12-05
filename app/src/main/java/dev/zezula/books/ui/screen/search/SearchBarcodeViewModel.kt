package dev.zezula.books.ui.screen.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.domain.FindBookForIsbnOnlineUseCase
import dev.zezula.books.domain.GetBookUseCase
import dev.zezula.books.domain.GetShelvesUseCase
import dev.zezula.books.domain.ToggleBookInShelfUseCase
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.ui.DestinationArgs
import dev.zezula.books.ui.whileSubscribedInActivity
import dev.zezula.books.util.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchBarcodeViewModel(
    private val findBookOnlineUseCase: FindBookForIsbnOnlineUseCase,
    private val toggleBookInShelfUseCase: ToggleBookInShelfUseCase,
    private val getBookUseCase: GetBookUseCase,
    private val getShelvesUseCase: GetShelvesUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val selectedShelfId: String? = savedStateHandle[DestinationArgs.shelfIdArg]
    private val isBulkScanningEnabled: Boolean? = savedStateHandle[DestinationArgs.isBulkScanOnArg]

    private val scannedIsbn = MutableStateFlow<String?>(null)
    private val selectedShelf = MutableStateFlow<Shelf?>(null)
    private val errorMessage = MutableStateFlow<Int?>(null)
    private val isBookSearchInProgress = MutableStateFlow(false)
    private val noBookWasFound = MutableStateFlow(false)
    private val foundedBookId = MutableStateFlow<String?>(null)
    private val foundedBook = MutableStateFlow<Book?>(null)

    val uiState = combine(
        scannedIsbn,
        selectedShelf,
        isBookSearchInProgress,
        errorMessage,
        noBookWasFound,
        foundedBookId,
        foundedBook,
    ) { barcode, selectedShelf, isSearchInProgress, errorMessage, noBookFound, foundBookId, foundedBook ->
        SearchBarcodeUiState(
            scannedIsbn = barcode,
            selectedShelf = selectedShelf,
            isSearchInProgress = isSearchInProgress,
            errorMessage = errorMessage,
            noBookFound = noBookFound,
            foundedBookId = foundBookId,
            foundedBook = foundedBook,
        )
    }
        .stateIn(viewModelScope, whileSubscribedInActivity, SearchBarcodeUiState())

    init {
        Timber.d("init{}")
        Timber.d("Selected Shelf ID: $selectedShelfId")
    }

    private fun searchBook(isbn: String) {
        viewModelScope.launch {
            isBookSearchInProgress.value = true
            var foundBookId: String? = null
            findBookOnlineUseCase(isbn)
                .fold(
                    onSuccess = { bookId ->
                        if (bookId != null) {
                            foundBookId = bookId
                            foundedBookId.value = bookId
                        } else {
                            noBookWasFound.value = true
                        }
                    },
                    onFailure = {
                        errorMessage.value = R.string.error_failed_get_data
                    },
                )
            if (isBulkScanningEnabled == true) {
                foundBookId?.let { bookId ->
                    foundedBook.value = getBookUseCase(bookId).getOrDefault(null)
                    if (selectedShelfId != null) {
                        selectedShelf.value = getShelvesUseCase()
                            .first()
                            .getOrDefault(emptyList())
                            .firstOrNull { it.id == selectedShelfId }
                        toggleBookInShelfUseCase(bookId = bookId, shelfId = selectedShelfId, isBookInShelf = true)
                    }
                }
            }
            isBookSearchInProgress.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun onIsbnScanned(isbn: String) {
        Timber.d("onIsbnScanned($isbn)")
        scannedIsbn.value = isbn
        searchBook(isbn)
    }

    fun scanAgain() {
        Timber.d("scanAgain()")
        noBookWasFound.value = false
        foundedBookId.value = null
        scannedIsbn.value = null
    }
}
