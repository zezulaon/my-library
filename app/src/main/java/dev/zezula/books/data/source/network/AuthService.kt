package dev.zezula.books.data.source.network

interface AuthService {

    fun getUserId(): String?

    fun isAccountAnonymous(): Boolean

    fun isUserSignedIn(): Boolean

    suspend fun googleSignIn(googleIdToken: String): Boolean

    suspend fun signInAnonymously(): Boolean

    suspend fun emailSignIn(email: String, password: String): EmailSignResult

    suspend fun emailCreateUser(email: String, password: String): EmailSignResult

    suspend fun requestPasswordReset(email: String): EmailSignResult
}

enum class EmailSignResult {
    Success,
    InvalidCredentials,
    InvalidUser,
    UserCollision,
    UnknownError,
}
