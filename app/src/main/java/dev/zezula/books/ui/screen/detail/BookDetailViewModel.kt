package dev.zezula.books.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.domain.AllBookDetailResult
import dev.zezula.books.domain.CheckReviewsDownloadedUseCase
import dev.zezula.books.domain.DeleteBookUseCase
import dev.zezula.books.domain.GetAllBookDetailUseCase
import dev.zezula.books.domain.ToggleBookInShelfUseCase
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.ui.DestinationArgs
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class BookDetailViewModel(
    private val deleteBookUseCase: DeleteBookUseCase,
    private val checkReviewsDownloadedUseCase: CheckReviewsDownloadedUseCase,
    private val toggleBookInShelfUseCase: ToggleBookInShelfUseCase,
    savedStateHandle: SavedStateHandle,
    getAllBookDetailUseCase: GetAllBookDetailUseCase,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle[DestinationArgs.bookIdArg])

    private val allBookDetail: Flow<Response<AllBookDetailResult>> = getAllBookDetailUseCase(bookId)
        .onResponseError { _errorMessage.value = R.string.error_failed_get_data }

    private val _errorMessage = MutableStateFlow<Int?>(null)
    private val _selectedTab = MutableStateFlow(DetailTab.Detail)
    private val _bookDeleted = MutableStateFlow(false)
    private val _isInProgress = MutableStateFlow(false)
    private val _isDeleteDialogDisplayed = MutableStateFlow(false)

    // Keeps shelf items that are being updated (in order to display progress or temporary check before
    // the updating is done)
    private val _shelvesToggleProgressList = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val uiState: StateFlow<BookDetailUiState> = dev.zezula.books.util.combine(
        allBookDetail,
        _selectedTab,
        _bookDeleted,
        _isInProgress,
        _errorMessage,
        _shelvesToggleProgressList,
        _isDeleteDialogDisplayed,
    ) { bookResponse, selectedTab, bookDeleted, isInProgress, errorMessage, shelvesToggleProgressList,
            isDeleteDialogDisplayed, ->

        val bookDetail = bookResponse.getOrDefault(AllBookDetailResult())
        BookDetailUiState(
            book = bookDetail.book,
            rating = bookDetail.rating,
            shelves = mergeShelvesWithToggleProgress(bookDetail.shelves, shelvesToggleProgressList),
            reviews = bookDetail.reviews,
            selectedTab = selectedTab,
            isBookDeleted = bookDeleted,
            errorMessage = errorMessage,
            isInProgress = isInProgress,
            isDeleteDialogDisplayed = isDeleteDialogDisplayed,
        )
    }.stateIn(viewModelScope, whileSubscribedInActivity, BookDetailUiState())

    private fun mergeShelvesWithToggleProgress(
        shelves: List<ShelfForBook>,
        shelvesToggleProgressList: Map<String, Boolean>,
    ): List<ShelfForBook> {
        val mutableShelves = shelves.toMutableList()
        shelvesToggleProgressList.forEach { toggleShelfMap ->
            mutableShelves.apply {
                val oldShelf = first { shelf -> shelf.id == toggleShelfMap.key }
                val index = indexOf(oldShelf)
                remove(oldShelf)
                add(index, oldShelf.copy(id = oldShelf.id, title = oldShelf.title, isBookAdded = toggleShelfMap.value))
            }
        }
        return mutableShelves
    }

    init {
        Timber.d("init{}")
        Timber.d("Received bookId: $bookId")
        fetchReviews()
    }

    private fun fetchReviews() {
        viewModelScope.launch {
            _isInProgress.value = true
            checkReviewsDownloadedUseCase(bookId)
            _isInProgress.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun onTabClick(tab: DetailTab) {
        _selectedTab.value = tab
    }

    fun onShelfCheckChange(shelfForBook: ShelfForBook, checked: Boolean) {
        Timber.d("onShelfCheckChange(shelf=$shelfForBook, checked=$checked)")
        viewModelScope.launch {
            _shelvesToggleProgressList.update { oldMap ->
                oldMap.toMutableMap().apply { put(shelfForBook.id, checked) }
            }
            toggleBookInShelfUseCase(bookId = bookId, shelfId = shelfForBook.id, isBookInShelf = checked)
                .onError {
                    _errorMessage.value = R.string.detail_failed_to_update_shelf
                }
            _shelvesToggleProgressList.update { oldMap ->
                oldMap.toMutableMap().apply { remove(shelfForBook.id) }
            }
        }
    }

    fun deleteBookRequested() {
        _isDeleteDialogDisplayed.value = true
    }

    fun deleteBookConfirmed() {
        viewModelScope.launch {
            deleteBookUseCase(bookId)
                .fold(
                    onSuccess = {
                        _bookDeleted.value = true
                    },
                    onFailure = { _errorMessage.value = R.string.detail_failed_to_delete },
                )
            _isDeleteDialogDisplayed.value = false
        }
    }

    fun dismissDeleteDialog() {
        _isDeleteDialogDisplayed.value = false
    }

    fun snackbarMessageShown() {
        _errorMessage.value = null
    }
}
