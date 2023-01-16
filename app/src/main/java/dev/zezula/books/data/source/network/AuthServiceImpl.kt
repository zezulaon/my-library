package dev.zezula.books.data.source.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthServiceImpl(private val auth: FirebaseAuth) : AuthService {

    override fun getUserId(): String? = auth.currentUser?.uid

    override fun isUserSignedIn(): Boolean = auth.currentUser != null

    override suspend fun googleSignIn(googleIdToken: String): Boolean {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return auth.signInWithCredential(firebaseCredential).await()?.user?.uid != null
    }

    override suspend fun signInAnonymously(): Boolean {
        return auth.signInAnonymously().await()?.user?.uid != null
    }
}