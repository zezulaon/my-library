package dev.zezula.books.ui.screen.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.source.network.AuthService
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class SignInViewModel(private val authService: AuthService) : ViewModel() {

    private val _errorMessage = MutableStateFlow<Int?>(null)
    private val _isUserSignedIn = MutableStateFlow(false)
    private val _isInProgress = MutableStateFlow(false)

    val uiState = combine(
        _isUserSignedIn,
        _isInProgress,
        _errorMessage,
    ) { userSignedIn, isInProgress, errorMessage ->
        SignInUiState(
            isUserSignedIn = userSignedIn,
            isSignInProgress = isInProgress,
            errorMessage = errorMessage,
        )
    }
        .stateIn(viewModelScope, whileSubscribedInActivity, SignInUiState())

    init {
        Timber.d("init{}")
        Timber.d("Current user: ${authService.getUserId()}")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun isSignedIn() = authService.isUserSignedIn()

    fun googleSignIn(googleIdToken: String) {
        viewModelScope.launch {
            _isInProgress.value = true
            val isSuccess = authService.googleSignIn(googleIdToken)
            _isInProgress.value = false
            if (isSuccess) {
                Timber.d("Sign In successful: ${authService.getUserId()}")
                _isUserSignedIn.value = true
            } else {
                _errorMessage.value = R.string.sign_in_google_sign_in_failed
                Timber.d("Sign In failed")
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _isInProgress.value = true
            val isSuccess = authService.signInAnonymously()
            _isInProgress.value = false
            if (isSuccess) {
                Timber.d("Anonymous Sign In successful: ${authService.getUserId()}")
                _isUserSignedIn.value = true
            } else {
                _errorMessage.value = R.string.sign_in_anonymous_sign_in_failed
                Timber.d("Anonymous Sign In failed")
            }
        }
    }

    fun onGoogleSignInClick() {
        _isInProgress.value = true
    }

    fun onGoogleSignInFailed() {
        _isInProgress.value = false
        _errorMessage.value = R.string.sign_in_google_sign_in_failed
    }

    fun snackbarMessageShown() {
        _errorMessage.value = null
    }
}
