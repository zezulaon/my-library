package dev.zezula.books.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.domain.AllBookDetailResult
import dev.zezula.books.domain.CheckReviewsDownloadedUseCase
import dev.zezula.books.domain.CreateOrUpdateNoteUseCase
import dev.zezula.books.domain.DeleteBookFromLibraryUseCase
import dev.zezula.books.domain.DeleteNoteUseCase
import dev.zezula.books.domain.FetchSuggestionsUseCase
import dev.zezula.books.domain.GetAllBookDetailUseCase
import dev.zezula.books.domain.MoveBookToLibraryUseCase
import dev.zezula.books.domain.RefreshBookCoverUseCase
import dev.zezula.books.domain.ToggleBookInShelfUseCase
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.ui.DestinationArgs
import dev.zezula.books.ui.whileSubscribedInActivity
import dev.zezula.books.util.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class BookDetailViewModel(
    private val moveBookToLibraryUseCase: MoveBookToLibraryUseCase,
    private val deleteBookUseCase: DeleteBookFromLibraryUseCase,
    private val checkReviewsDownloadedUseCase: CheckReviewsDownloadedUseCase,
    private val fetchSuggestionsUseCase: FetchSuggestionsUseCase,
    private val createOrUpdateNoteUseCase: CreateOrUpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val toggleBookInShelfUseCase: ToggleBookInShelfUseCase,
    private val refreshBookCoverUseCase: RefreshBookCoverUseCase,
    savedStateHandle: SavedStateHandle,
    getAllBookDetailUseCase: GetAllBookDetailUseCase,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle[DestinationArgs.bookIdArg])

    private val allBookDetail: Flow<Response<AllBookDetailResult>> = getAllBookDetailUseCase(bookId)
        .onResponseError { _errorMessage.value = R.string.error_failed_get_data }

    private val _errorMessage = MutableStateFlow<Int?>(null)
    private val _selectedTab = MutableStateFlow(DetailTab.Detail)
    private val _bookDeleted = MutableStateFlow(false)
    private val _isReviewsSearchInProgress = MutableStateFlow(false)
    private val _isDeleteDialogDisplayed = MutableStateFlow(false)
    private val _isNoteDialogDisplayed = MutableStateFlow(false)
    private val _selectedNote = MutableStateFlow<Note?>(null)
    private val _addBookToLibraryInProgress = MutableStateFlow(false)

    private val _suggestionsInProgress = MutableStateFlow(false)
    private val _suggestionsProgress = MutableStateFlow(.0f)
    private val _suggestionsRefreshFailed = MutableStateFlow(false)
    private val suggestionsUiState: Flow<SuggestionsUiState> = combine(
        _suggestionsInProgress,
        _suggestionsProgress,
        _suggestionsRefreshFailed,
        allBookDetail,
    ) { suggestionsInProgress, suggestionsProgress, suggestionsRefreshFailed, bookResponse ->
        val bookDetail = bookResponse.getOrDefault(AllBookDetailResult())
        SuggestionsUiState(
            suggestions = bookDetail.suggestions,
            progress = suggestionsProgress,
            refreshFailed = suggestionsRefreshFailed,
            isGeneratingInProgress = suggestionsInProgress,
        )
    }

    // Keeps shelf items that are being updated (in order to display progress or temporary check before
    // the updating is done)
    private val _shelvesToggleProgressList = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val uiState: StateFlow<BookDetailUiState> = combine(
        allBookDetail,
        suggestionsUiState,
        _selectedTab,
        _bookDeleted,
        _isReviewsSearchInProgress,
        _errorMessage,
        _shelvesToggleProgressList,
        _isDeleteDialogDisplayed,
        _isNoteDialogDisplayed,
        _selectedNote,
    ) { bookResponse, suggestionUiState, selectedTab, bookDeleted, isReviewsSearchInProgress, errorMessage,
            shelvesToggleProgressList, isDeleteDialogDisplayed, isNewNoteDialogDisplayed, selectedNote, ->

        val bookDetail = bookResponse.getOrDefault(AllBookDetailResult())
        BookDetailUiState(
            book = bookDetail.book,
            isBookInLibrary = bookDetail.isBookInLibrary,
            rating = bookDetail.rating,
            notes = bookDetail.notes,
            shelves = mergeShelvesWithToggleProgress(bookDetail.shelves, shelvesToggleProgressList),
            reviews = bookDetail.reviews,
            suggestionsUiState = suggestionUiState,
            selectedTab = selectedTab,
            isBookDeleted = bookDeleted,
            errorMessage = errorMessage,
            isReviewsSearchInProgress = isReviewsSearchInProgress,
            isDeleteDialogDisplayed = isDeleteDialogDisplayed,
            isNewNoteDialogDisplayed = isNewNoteDialogDisplayed,
            selectedNote = selectedNote,
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
        fetchBookCover()
    }

    fun fetchReviews() {
        viewModelScope.launch {
            _isReviewsSearchInProgress.value = true
            checkReviewsDownloadedUseCase(bookId)
            _isReviewsSearchInProgress.value = false
        }
    }

    private fun fetchBookCover() {
        viewModelScope.launch {
            refreshBookCoverUseCase(bookId)
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

    fun createNoteRequested() {
        _isNoteDialogDisplayed.value = true
    }

    fun dismissNoteDialog() {
        _isNoteDialogDisplayed.value = false
        _selectedNote.value = null
    }

    fun createNote(text: String) {
        viewModelScope.launch {
            val noteFormData = NoteFormData(text = text)
            createOrUpdateNoteUseCase(noteId = null, noteFormData = noteFormData, bookId = bookId)
                .onError {
                    _errorMessage.value = R.string.detail_failed_to_create_note
                }
            _isNoteDialogDisplayed.value = false
        }
    }

    fun updateNote(note: Note, text: String) {
        dismissNoteDialog()
        viewModelScope.launch {
            val noteFormData = NoteFormData(text = text, dateAdded = note.dateAdded, page = note.page, type = note.type)
            createOrUpdateNoteUseCase(noteId = note.id, noteFormData = noteFormData, bookId = bookId)
                .onError {
                    _errorMessage.value = R.string.detail_failed_to_update_note
                }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId = note.id, bookId = bookId)
                .onError {
                    _errorMessage.value = R.string.detail_failed_to_delete_note
                }
        }
    }

    fun editNoteRequested(note: Note) {
        _isNoteDialogDisplayed.value = true
        _selectedNote.value = note
    }

    fun addBookToLibrary() {
        if (_addBookToLibraryInProgress.value) return

        viewModelScope.launch {
            _addBookToLibraryInProgress.value = true
            moveBookToLibraryUseCase(bookId)
                .onError {
                    _errorMessage.value = R.string.detail_failed_to_add_book
                }
            _addBookToLibraryInProgress.value = false
        }
    }

    fun generateSuggestions() {
        viewModelScope.launch {
            _suggestionsInProgress.value = true
            _suggestionsProgress.value = .95f

            fetchSuggestionsUseCase(bookId).fold(
                onSuccess = {
                    if (it.isNullOrEmpty()) {
                        _suggestionsRefreshFailed.value = true
                    }
                },
                onFailure = {
                    _suggestionsRefreshFailed.value = true
                },
            )
            _suggestionsInProgress.value = false
        }
    }
}
