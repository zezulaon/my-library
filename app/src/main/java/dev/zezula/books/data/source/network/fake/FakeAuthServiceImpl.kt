package dev.zezula.books.data.source.network.fake

import dev.zezula.books.data.source.network.AuthService
import dev.zezula.books.data.source.network.EmailSignResult

class FakeAuthServiceImpl : AuthService {

    override fun getUserId(): String = "123"

    override fun isUserSignedIn(): Boolean = true

    override fun isAccountAnonymous(): Boolean = false

    override suspend fun googleSignIn(googleIdToken: String): Boolean = true

    override suspend fun signInAnonymously(): Boolean = true

    override suspend fun emailSignIn(email: String, password: String) = EmailSignResult.Success

    override suspend fun emailCreateUser(email: String, password: String) = EmailSignResult.Success

    override suspend fun requestPasswordReset(email: String) = EmailSignResult.Success
}
