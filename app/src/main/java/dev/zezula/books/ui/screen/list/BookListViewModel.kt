package dev.zezula.books.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.domain.GetBooksForShelfUseCase
import dev.zezula.books.domain.GetShelvesUseCase
import dev.zezula.books.domain.RefreshLibraryUseCase
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class BookListViewModel(
    getShelvesUseCase: GetShelvesUseCase,
    private val getBooksForShelfUseCase: GetBooksForShelfUseCase,
    private val refreshLibraryUseCase: RefreshLibraryUseCase,
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<Int?>(null)
    private val _selectedShelf = MutableStateFlow<Shelf?>(null)
    private val _managedShelvesClicked = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val booksForShelf: Flow<Response<List<Book>>> = _selectedShelf
        .flatMapLatest { getBooksForShelfUseCase(it) }
        .onResponseError { _errorMessage.value = R.string.error_failed_get_data }

    private val shelves: Flow<Response<List<Shelf>>> = getShelvesUseCase()
        .onResponseError { _errorMessage.value = R.string.error_failed_get_data }


    val uiState: StateFlow<BookListUiState> =
        combine(
            _errorMessage,
            _selectedShelf,
            _managedShelvesClicked,
            booksForShelf,
            shelves,
        ) { errorMsg, selectedShelfId, managedShelvesClicked, books, shelves ->
            BookListUiState(
                books = books.getOrDefault(emptyList()),
                shelves = shelves.getOrDefault(emptyList()),
                selectedShelf = selectedShelfId,
                managedShelvesClicked = managedShelvesClicked,
                errorMessage = errorMsg
            )
        }
            .stateIn(
                scope = this.viewModelScope,
                started = whileSubscribedInActivity,
                initialValue = BookListUiState()
            )

    init {
        Timber.d("init{}")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun refresh() {
        Timber.d("refresh()")
        viewModelScope.launch {
            refreshLibraryUseCase().fold(
                onSuccess = { Timber.d("refresh() - successful") },
                onFailure = { _errorMessage.value = R.string.home_failed_to_refresh }
            )
        }
    }

    fun onShelfSelected(selectedShelf: Shelf) {
        _selectedShelf.value = selectedShelf
    }

    fun onAllBooksShelfSelected() {
        _selectedShelf.value = null
    }

    fun snackbarMessageShown() {
        _errorMessage.value = null
    }

    fun onManagedShelvesClicked() {
        _managedShelvesClicked.value = true
    }

    fun onManagedShelvesClickedHandled() {
        _managedShelvesClicked.value = false
    }
}