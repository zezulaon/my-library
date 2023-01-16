package dev.zezula.books.ui.screen.signin

data class SignInUiState(
    val isSignInProgress: Boolean = false,
    val isUserSignedIn: Boolean = false,
    val errorMessage: Int? = null,
)
