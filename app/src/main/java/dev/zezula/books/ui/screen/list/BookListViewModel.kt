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

    private val errorMessage = MutableStateFlow<Int?>(null)
    private val selectedShelf = MutableStateFlow<Shelf?>(null)
    private val drawerItemClicked = MutableStateFlow<DrawerClickItem?>(null)
    private val addBookSheetOpened = MutableStateFlow(false)
    private val moreDialogDisplayed = MutableStateFlow(false)
    private val sortBooksDialogDisplayed = MutableStateFlow(false)
    private val sortBooksBy = MutableStateFlow(SortBooksBy.DATE_ADDED)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val booksForShelf: Flow<Response<List<Book>>> =
        combine(selectedShelf, sortBooksBy) { shelf, sortBooksBy -> ShelfAndSorting(shelf, sortBooksBy) }
            .flatMapLatest { shelfAndSorting ->
                Timber.d("Getting books for shelf and sorting: $shelfAndSorting")
                getBooksForShelfUseCase(shelfAndSorting.shelf, shelfAndSorting.sortBooksBy)
            }
            .onResponseError { errorMessage.value = R.string.error_failed_get_data }

    private val shelves: Flow<Response<List<Shelf>>> = getShelvesUseCase()
        .onResponseError { errorMessage.value = R.string.error_failed_get_data }

    private val drawerNavigationFlow = combine(
        drawerItemClicked,
        shelves,
    ) { drawerItemClicked, shelves ->
        DrawerNavigationState(
            shelves = shelves.getOrDefault(emptyList()),
            drawerItemClicked = drawerItemClicked,
        )
    }

    private val sortingFlow = combine(
        sortBooksDialogDisplayed,
        sortBooksBy,
        booksForShelf,
    ) { sortDialogDisplayed, sortBooksBy, books ->
        SortingState(
            sortDialogDisplayed = sortDialogDisplayed,
            sortBooksBy = sortBooksBy,
            canSortByRating = books.getOrDefault(emptyList()).any { it.userRating != null },
        )
    }

    private val infoMessagesFlow = combine(
        addBookSheetOpened,
        moreDialogDisplayed,
        errorMessage,
    ) { addBookSheetOpened, moreDialogDisplayed, errorMessage ->
        InfoMessagesState(
            addBookSheetOpened = addBookSheetOpened,
            moreDialogDisplayed = moreDialogDisplayed,
            errorMessage = errorMessage,
        )
    }

    val uiState: StateFlow<BookListUiState> =
        combine(
            booksForShelf,
            drawerNavigationFlow,
            sortingFlow,
            infoMessagesFlow,
            selectedShelf,
        ) { books, drawerNavigation, sorting, infoMessages, selectedShelfId ->
            BookListUiState(
                books = books.getOrDefault(emptyList()),
                drawerNavigation = drawerNavigation,
                sorting = sorting,
                infoMessages = infoMessages,
                selectedShelf = selectedShelfId,
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
                onFailure = { errorMessage.value = R.string.home_failed_to_refresh },
            )
        }
    }

    fun onShelfSelected(selectedShelf: Shelf) {
        this.selectedShelf.value = selectedShelf
    }

    fun onAllBooksShelfSelected() {
        selectedShelf.value = null
    }

    fun snackbarMessageShown() {
        errorMessage.value = null
    }

    fun onDrawerItemClicked(drawerClickItem: DrawerClickItem) {
        drawerItemClicked.value = drawerClickItem
    }

    fun onDrawerItemClickedHandled() {
        drawerItemClicked.value = null
    }

    fun onAddBookSheetOpenRequest() {
        addBookSheetOpened.value = true
    }

    fun onAddBookSheetDismissRequest() {
        addBookSheetOpened.value = false
    }

    fun onMoreClicked() {
        moreDialogDisplayed.value = true
    }

    fun onAboutDialogDismissRequest() {
        moreDialogDisplayed.value = false
    }

    fun onSortBooksClicked() {
        sortBooksDialogDisplayed.value = true
    }

    fun onSortDialogDismissRequest() {
        sortBooksDialogDisplayed.value = false
    }

    fun onSortBooksSelected(selectedSorting: SortBooksBy) {
        sortBooksBy.value = selectedSorting
        onSortDialogDismissRequest()
    }
}
