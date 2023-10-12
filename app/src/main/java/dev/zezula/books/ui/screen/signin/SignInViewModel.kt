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

    private val uiMessage = MutableStateFlow<Int?>(null)
    private val isUserSignedIn = MutableStateFlow(false)
    private val isInProgress = MutableStateFlow(false)
    private val anonymUpgradeRequired = MutableStateFlow(false)

    val uiState = combine(
        isUserSignedIn,
        isInProgress,
        uiMessage,
        anonymUpgradeRequired,
    ) { userSignedIn, isInProgress, uiMessage, upgradeAnonymCardDismissed ->
        SignInUiState(
            isUserSignedIn = userSignedIn,
            isSignInProgress = isInProgress,
            uiMessage = uiMessage,
            anonymUpgradeRequired = upgradeAnonymCardDismissed,
        )
    }
        .stateIn(viewModelScope, whileSubscribedInActivity, SignInUiState())

    init {
        Timber.d("init{}")
        Timber.d("Current user: ${authService.getUserId()}")
        anonymUpgradeRequired.value = authService.isAccountAnonymous()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared()")
    }

    fun onAnonymUpgradeDismissed() {
        anonymUpgradeRequired.value = false
    }

    fun isSignedIn() = authService.isUserSignedIn()

    fun googleSignIn(googleIdToken: String) {
        viewModelScope.launch {
            isInProgress.value = true
            val isSuccess = authService.googleSignIn(googleIdToken)
            isInProgress.value = false
            if (isSuccess) {
                Timber.d("Sign In successful: ${authService.getUserId()}")
                isUserSignedIn.value = true
                anonymUpgradeRequired.value = authService.isAccountAnonymous()
                uiMessage.value = R.string.sign_in_google_sign_in_successful
            } else {
                uiMessage.value = R.string.sign_in_google_sign_in_failed
                Timber.d("Sign In failed")
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            isInProgress.value = true
            val isSuccess = authService.signInAnonymously()
            isInProgress.value = false
            if (isSuccess) {
                Timber.d("Anonymous Sign In successful: ${authService.getUserId()}")
                isUserSignedIn.value = true
                anonymUpgradeRequired.value = authService.isAccountAnonymous()
            } else {
                uiMessage.value = R.string.sign_in_anonymous_sign_in_failed
                Timber.d("Anonymous Sign In failed")
            }
        }
    }

    fun onGoogleSignInClick() {
        isInProgress.value = true
    }

    fun onGoogleSignInFailed() {
        isInProgress.value = false
        uiMessage.value = R.string.sign_in_google_sign_in_failed
    }

    fun snackbarMessageShown() {
        uiMessage.value = null
    }
}
