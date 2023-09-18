package dev.zezula.books.data.source.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthServiceImpl(private val auth: FirebaseAuth) : AuthService {

    override fun getUserId(): String? = auth.currentUser?.uid

    override fun isUserSignedIn(): Boolean = auth.currentUser != null

    override suspend fun googleSignIn(googleIdToken: String): Boolean {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            auth.signInWithCredential(firebaseCredential).await()?.user?.uid != null
        } catch (e: Exception) {
            Timber.e("Failed to sign in with Google.", e)
            false
        }
    }

    override suspend fun signInAnonymously(): Boolean {
        return try {
            auth.signInAnonymously().await()?.user?.uid != null
        } catch (e: Exception) {
            Timber.e("Failed to sign in anonymously.", e)
            false
        }
    }
}
