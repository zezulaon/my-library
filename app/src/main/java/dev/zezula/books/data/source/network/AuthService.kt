package dev.zezula.books.data.source.network

interface AuthService {

    fun getUserId(): String?

    fun isUserSignedIn(): Boolean

    suspend fun googleSignIn(googleIdToken: String): Boolean

    suspend fun signInAnonymously(): Boolean
}
