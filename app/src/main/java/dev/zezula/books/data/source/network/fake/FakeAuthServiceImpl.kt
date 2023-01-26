package dev.zezula.books.data.source.network.fake

import dev.zezula.books.data.source.network.AuthService

class FakeAuthServiceImpl : AuthService {

    override fun getUserId(): String = "123"

    override fun isUserSignedIn(): Boolean = true

    override suspend fun googleSignIn(googleIdToken: String): Boolean = true

    override suspend fun signInAnonymously(): Boolean = true
}
