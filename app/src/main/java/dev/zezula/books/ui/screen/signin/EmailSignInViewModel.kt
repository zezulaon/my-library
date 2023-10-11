package dev.zezula.books.ui.screen.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.R
import dev.zezula.books.data.source.network.AuthService
import dev.zezula.books.data.source.network.EmailSignResult
import dev.zezula.books.ui.whileSubscribedInActivity
import dev.zezula.books.util.combine
import dev.zezula.books.util.isValidEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class EmailSignInViewModel(private val authService: AuthService) : ViewModel() {

    private val _selectedMode = MutableStateFlow(EmailSignMode.SignIn)
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _uiMessage = MutableStateFlow<Int?>(null)
    private val _isUserSignedIn = MutableStateFlow(false)
    private val _isInProgress = MutableStateFlow(false)
    private val _invalidEmail = MutableStateFlow(false)
    private val _invalidPassword = MutableStateFlow(false)

    val uiState = combine(
        _selectedMode,
        _email,
        _password,
        _isUserSignedIn,
        _isInProgress,
        _uiMessage,
        _invalidEmail,
        _invalidPassword,
    ) { selectedMode, email, password, userSignedIn, isInProgress, errorMessage, invalidEmail, invalidPassword ->
        EmailSignInUiState(
            mode = selectedMode,
            email = email,
            password = password,
            isUserSignedIn = userSignedIn,
            isSignInProgress = isInProgress,
            uiMessage = errorMessage,
            invalidEmail = invalidEmail,
            invalidPassword = invalidPassword,
        )
    }
        .stateIn(viewModelScope, whileSubscribedInActivity, EmailSignInUiState())

    fun onSignInClicked() {
        // Continue only if we have all form input data
        if (isInvalidForm()) return

        viewModelScope.launch {
            _isInProgress.value = true
            val result = authService.emailSignIn(email = _email.value, password = _password.value)
            _isInProgress.value = false
            Timber.d("Email Sign In result: $result")
            when (result) {
                EmailSignResult.Success -> {
                    Timber.d("Email Sign In successful: ${authService.getUserId()}")
                    _isUserSignedIn.value = true
                }
                EmailSignResult.InvalidCredentials -> _uiMessage.value = R.string.sign_in_email_failed_invalid_credentials
                EmailSignResult.InvalidUser -> _uiMessage.value = R.string.sign_in_email_failed_invalid_user
                else -> _uiMessage.value = R.string.sign_in_email_sign_in_failed
            }
        }
    }

    fun onRegisterClicked() {
        // Continue only if we have all form input data
        if (isInvalidForm()) return

        viewModelScope.launch {
            _isInProgress.value = true
            val result = authService.emailCreateUser(email = _email.value, password = _password.value)
            _isInProgress.value = false
            Timber.d("Email Register In result: $result")
            when (result) {
                EmailSignResult.Success -> {
                    Timber.d("Email registration successful: ${authService.getUserId()}")
                    _isUserSignedIn.value = true
                }
                EmailSignResult.UserCollision -> _uiMessage.value = R.string.sign_in_email_register_collision
                else -> {
                    _uiMessage.value = R.string.sign_in_email_register_failed
                    Timber.d("Email registration failed")
                }
            }
        }
    }

    fun onRequestPasswordResetClicked() {
        _invalidPassword.value = false

        // Continue only if we have valid email
        if (_email.value.isValidEmail().not()) {
            _invalidEmail.value = true
            return
        }

        viewModelScope.launch {
            _isInProgress.value = true
            val result = authService.requestPasswordReset(email = _email.value)
            _isInProgress.value = false
            Timber.d("Email reset result: $result")
            when (result) {
                EmailSignResult.Success -> {
                    _uiMessage.value = R.string.sign_in_password_reset_sent_to
                    Timber.d("Password reset successful: ${authService.getUserId()}")
                }

                EmailSignResult.InvalidUser -> _uiMessage.value = R.string.sign_in_email_failed_invalid_user
                else -> {
                    _uiMessage.value = R.string.sign_in_password_reset_failed
                    Timber.d("Password reset failed")
                }
            }
        }
    }

    private fun isInvalidForm(): Boolean {
        val isEmailValid = _email.value.isValidEmail()
        val isPasswordValid = _password.value.length >= 6

        Timber.d("isEmailValid=$isEmailValid, isPasswordValid=$isPasswordValid")

        _invalidEmail.value = isEmailValid.not()
        _invalidPassword.value = isPasswordValid.not()

        return isEmailValid.not() || isPasswordValid.not()
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun onCreateAccountClicked() {
        _selectedMode.value = EmailSignMode.Register
    }

    /**
     * Checks if we are on Register screen [EmailSignMode.Register] and if so, goes back to Sign In
     * screen [EmailSignMode.SignIn].
     *
     * @return true if back was handled, false otherwise
     */
    fun onBackClicked(): Boolean {
        return if (_selectedMode.value == EmailSignMode.Register) {
            _selectedMode.value = EmailSignMode.SignIn
            true
        } else {
            false
        }
    }

    fun snackbarMessageShown() = _uiMessage.update { null }
}
