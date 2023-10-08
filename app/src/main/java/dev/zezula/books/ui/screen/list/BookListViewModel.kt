package dev.zezula.books.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.SortBooksBy
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.domain.GetBooksForShelfUseCase
import dev.zezula.books.domain.GetShelvesUseCase
import dev.zezula.books.domain.RefreshLibraryUseCase
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.ui.whileSubscribedInActivity
import dev.zezula.books.util.combine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
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
    private val _allAuthorsClicked = MutableStateFlow(false)
    private val _addBookSheetOpened = MutableStateFlow(false)
    private val _moreDialogDisplayed = MutableStateFlow(false)
    private val _sortBooksDialogDisplayed = MutableStateFlow(false)
    private val _sortBooksBy = MutableStateFlow(SortBooksBy.DATE_ADDED)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val booksForShelf: Flow<Response<List<Book>>> =
        combine(_selectedShelf, _sortBooksBy) { shelf, sortBooksBy -> ShelfAndSorting(shelf, sortBooksBy) }
            .flatMapLatest { shelfAndSorting ->
                Timber.d("Getting books for shelf and sorting: $shelfAndSorting")
                getBooksForShelfUseCase(shelfAndSorting.shelf, shelfAndSorting.sortBooksBy)
            }
            .onResponseError { _errorMessage.value = R.string.error_failed_get_data }

    private val shelves: Flow<Response<List<Shelf>>> = getShelvesUseCase()
        .onResponseError { _errorMessage.value = R.string.error_failed_get_data }

    val uiState: StateFlow<BookListUiState> =
        combine(
            _errorMessage,
            _selectedShelf,
            _managedShelvesClicked,
            _allAuthorsClicked,
            _addBookSheetOpened,
            _moreDialogDisplayed,
            _sortBooksDialogDisplayed,
            _sortBooksBy,
            booksForShelf,
            shelves,
        ) {
                errorMsg, selectedShelfId, managedShelvesClicked, allAuthorsClicked,
                addBookSheetOpened, moreDialogDisplayed,
                sortBooksDialogDisplayed, sortBooksBy, books, shelves,
            ->
            BookListUiState(
                books = books.getOrDefault(emptyList()),
                shelves = shelves.getOrDefault(emptyList()),
                selectedShelf = selectedShelfId,
                managedShelvesClicked = managedShelvesClicked,
                allAuthorsClicked = allAuthorsClicked,
                addBookSheetOpened = addBookSheetOpened,
                moreDialogDisplayed = moreDialogDisplayed,
                sortDialogDisplayed = sortBooksDialogDisplayed,
                sortBooksBy = sortBooksBy,
                errorMessage = errorMsg,
            )
        }
            .stateIn(
                scope = this.viewModelScope,
                started = whileSubscribedInActivity,
                initialValue = BookListUiState(),
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
                onFailure = { _errorMessage.value = R.string.home_failed_to_refresh },
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

    fun onAllAuthorsClicked() {
        _allAuthorsClicked.value = true
    }

    fun onAllAuthorsClickedHandled() {
        _allAuthorsClicked.value = false
    }

    fun onAddBookSheetOpenRequest() {
        _addBookSheetOpened.value = true
    }

    fun onAddBookSheetDismissRequest() {
        _addBookSheetOpened.value = false
    }

    fun onMoreClicked() {
        _moreDialogDisplayed.value = true
    }

    fun onAboutDialogDismissRequest() {
        _moreDialogDisplayed.value = false
    }

    fun onSortBooksClicked() {
        _sortBooksDialogDisplayed.value = true
    }

    fun onSortDialogDismissRequest() {
        _sortBooksDialogDisplayed.value = false
    }

    fun onSortBooksSelected(selectedSorting: SortBooksBy) {
        _sortBooksBy.value = selectedSorting
        onSortDialogDismissRequest()
    }
}
