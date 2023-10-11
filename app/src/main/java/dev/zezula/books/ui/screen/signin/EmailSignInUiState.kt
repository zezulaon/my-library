package dev.zezula.books.ui.screen.signin

data class EmailSignInUiState(
    val mode: EmailSignMode = EmailSignMode.SignIn,
    val email: String = "",
    val password: String = "",
    val isSignInProgress: Boolean = false,
    val isUserSignedIn: Boolean = false,
    val uiMessage: Int? = null,
    val invalidEmail: Boolean = false,
    val invalidPassword: Boolean = false,
) {

    val isSignInButtonEnabled = !isSignInProgress && email.isNotEmpty() && password.isNotEmpty()

    val areEditTextsEnabled = !isSignInProgress
}
