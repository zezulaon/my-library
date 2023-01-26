package dev.zezula.books.ui.screen.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.domain.AddOrUpdateBookUseCase
import dev.zezula.books.domain.GetBooksUseCase
import dev.zezula.books.domain.model.getOrDefault
import dev.zezula.books.ui.DestinationArgs
import dev.zezula.books.ui.whileSubscribedInActivity
import dev.zezula.books.util.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateBookViewModel(
    savedStateHandle: SavedStateHandle,
    private val GetBooksUseCase: GetBooksUseCase,
    private val addOrUpdateBookUseCase: AddOrUpdateBookUseCase,
) : ViewModel() {

    private val bookId: String?

    private val _bookFormData = MutableStateFlow(BookFormData())
    private val _errorMessage = MutableStateFlow<Int?>(null)
    private val _isInEditMode = MutableStateFlow(false)
    private val _isInProgress = MutableStateFlow(false)
    private val _isBookSaved = MutableStateFlow(false)
    private val _invalidForm = MutableStateFlow(false)

    val uiState = combine(
        _bookFormData,
        _errorMessage,
        _isInEditMode,
        _isInProgress,
        _isBookSaved,
        _invalidForm,
    ) { bookFormData, errorMessage, isInEditMode, isInProgress, isBookSaved, invalidForm ->
        CreateBookUiState(
            bookFormData = bookFormData,
            isInEditMode = isInEditMode,
            isInProgress = isInProgress,
            isBookSaved = isBookSaved,
            errorMessage = errorMessage,
            invalidForm = invalidForm,
        )
    }
        .stateIn(viewModelScope, whileSubscribedInActivity, CreateBookUiState())

    init {
        val bookIdArg: String? = savedStateHandle[DestinationArgs.bookIdArg]
        bookId = if (bookIdArg != null && bookIdArg != "null") {
            bookIdArg
        } else {
            null
        }
        if (bookId != null) {
            Timber.d("Edit mode - received [bookId=$bookId]")
        } else {
            Timber.d("Create mode - no [bookId] received")
        }
        loadBook()
        Timber.d("init{}")
    }

    private fun loadBook() {
        if (bookId != null) {
            viewModelScope.launch {
                _isInProgress.value = true

                val book = GetBooksUseCase(bookId).getOrDefault(null)
                if (book != null) {
                    _isInEditMode.value = true
                    _bookFormData.value = BookFormData(
                        title = book.title,
                        author = book.author,
                        isbn = book.isbn,
                        publisher = book.publisher,
                        yearPublished = book.yearPublished,
                        pageCount = book.pageCount,
                        description = book.description,
                    )
                }

                _isInProgress.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun saveBook() {
        // Continue only if we have all form input data
        if (isInvalidFormValid()) return

        viewModelScope.launch {
            _isInProgress.value = true
            addOrUpdateBookUseCase(bookId = bookId, bookFormData = _bookFormData.value)
                .fold(
                    onSuccess = { _isBookSaved.value = true },
                    onFailure = { _errorMessage.value = R.string.create_book_failed_to_save },
                )
            _isInProgress.value = false
        }
    }

    private fun isInvalidFormValid(): Boolean {
        val isInvalid = _bookFormData.value.title.isNullOrEmpty()
        if (isInvalid) {
            _invalidForm.value = true
            _errorMessage.value = R.string.invalid_input_form
        }
        return isInvalid
    }

    fun snackbarMessageShown() = _errorMessage.update { null }

    fun updateTitle(updatedTitle: String) = _bookFormData.update { it.copy(title = updatedTitle) }
    fun updateAuthor(updatedAuthor: String) = _bookFormData.update { it.copy(author = updatedAuthor) }
    fun updateDescription(updatedDesc: String) = _bookFormData.update { it.copy(description = updatedDesc) }
    fun updateIsbn(updatedIsbn: String) = _bookFormData.update { it.copy(isbn = updatedIsbn) }
    fun updatePublisher(updatedPublisher: String) {
        _bookFormData.update { it.copy(publisher = updatedPublisher) }
    }

    fun updateYearPublished(updatedYearPublished: String) {
        val year: Int? = updatedYearPublished.toIntOrNull()
        _bookFormData.update { it.copy(yearPublished = year) }
    }

    fun updatePageCount(updatedPageCount: String) {
        val pageCount: Int? = updatedPageCount.toIntOrNull()
        _bookFormData.update { it.copy(pageCount = pageCount) }
    }
}
