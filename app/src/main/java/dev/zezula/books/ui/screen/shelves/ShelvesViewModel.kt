package dev.zezula.books.ui.screen.shelves

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.domain.CreateShelfUseCase
import dev.zezula.books.domain.DeleteShelfUseCase
import dev.zezula.books.domain.GetShelvesUseCase
import dev.zezula.books.domain.UpdateShelfUseCase
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class ShelvesViewModel(
    private val createShelfUseCase: CreateShelfUseCase,
    private val updateShelfUseCase: UpdateShelfUseCase,
    private val deleteShelfUseCase: DeleteShelfUseCase,
    getShelvesUseCase: GetShelvesUseCase,
) : ViewModel() {

    private val _showShelfDialog = MutableStateFlow(false)
    private val _selectedShelf = MutableStateFlow<Shelf?>(null)
    private val _errorMessage = MutableStateFlow<Int?>(null)

    private val shelves: Flow<Response<List<Shelf>>> = getShelvesUseCase()
        .onResponseError { _errorMessage.value = R.string.error_failed_get_data }

    val uiState: StateFlow<ShelvesUiState> =
        combine(
            shelves,
            _showShelfDialog,
            _selectedShelf,
            _errorMessage,
        ) { shelves, showAddNewShelf, selectedShelf, errorMessage ->
            ShelvesUiState(
                shelves = shelves.getOrDefault(emptyList()),
                showAddOrEditShelfDialog = showAddNewShelf,
                selectedShelf = selectedShelf,
                errorMessage = errorMessage,
            )
        }
            .stateIn(
                scope = this.viewModelScope,
                started = whileSubscribedInActivity,
                initialValue = ShelvesUiState()
            )

    init {
        Timber.d("init{}")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun createShelf(shelfTitle: String) {
        onShelfDialogDismissed()
        viewModelScope.launch {
            createShelfUseCase(shelfTitle)
                .onError { _errorMessage.value = R.string.shelves_failed_to_create_shelf }
        }
    }

    fun updateShelf(shelf: Shelf, updatedTitle: String) {
        onShelfDialogDismissed()
        viewModelScope.launch {
            updateShelfUseCase(shelfId = shelf.id, updatedTitle = updatedTitle)
                .onError { _errorMessage.value = R.string.shelves_failed_to_update_shelf }
        }
    }

    fun deleteShelf(shelf: Shelf) {
        viewModelScope.launch {
            deleteShelfUseCase(shelf)
                .onError { _errorMessage.value = R.string.shelves_failed_to_delete_shelf }
        }
    }

    fun onAddNewShelfClick() {
        _showShelfDialog.value = true
    }

    fun onEditShelfClicked(shelf: Shelf) {
        _showShelfDialog.value = true
        _selectedShelf.value = shelf
    }

    fun onShelfDialogDismissed() {
        _showShelfDialog.value = false
        _selectedShelf.value = null
    }

    fun snackbarMessageShown() {
        _errorMessage.value = null
    }
}