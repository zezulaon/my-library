package dev.zezula.books.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import dev.zezula.books.domain.services.AuthService
import dev.zezula.books.domain.services.EmailSignResult
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthServiceImpl(private val auth: FirebaseAuth) : AuthService {

    override fun getUserId(): String? = auth.currentUser?.uid

    override fun isUserSignedIn(): Boolean = auth.currentUser != null

    override fun isAccountAnonymous(): Boolean {
        return auth.currentUser?.isAnonymous == true
    }

    override suspend fun googleSignIn(googleIdToken: String): Boolean {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val existingUser = auth.currentUser
            if (existingUser != null && existingUser.isAnonymous) {
                Timber.d("Linking anonymous account with Google.")
                existingUser.linkWithCredential(firebaseCredential).await()?.user?.uid != null
            } else {
                Timber.d("Signing in with Google.")
                auth.signInWithCredential(firebaseCredential).await()?.user?.uid != null
            }
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

    override suspend fun emailSignIn(email: String, password: String): EmailSignResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()?.user?.uid != null
            if (result) {
                EmailSignResult.Success
            } else {
                EmailSignResult.UnknownError
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Wrong password
            Timber.e("Failed to sign in using email (InvalidCredentials): ${e.errorCode}", e)
            return EmailSignResult.InvalidCredentials
        } catch (e: FirebaseAuthInvalidUserException) {
            // User probably doesn't exist
            Timber.e("Failed to sign in using email (InvalidUser): ${e.errorCode}", e)
            return EmailSignResult.InvalidUser
        } catch (e: Exception) {
            Timber.e("Failed to sign in using email/password.", e)
            return EmailSignResult.UnknownError
        }
    }

    override suspend fun emailCreateUser(email: String, password: String): EmailSignResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()?.user?.uid != null
            if (result) {
                EmailSignResult.Success
            } else {
                EmailSignResult.UnknownError
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            // User already exists
            Timber.e("Failed to create account (UserCollision): ${e.errorCode}", e)
            return EmailSignResult.UserCollision
        } catch (e: Exception) {
            Timber.e("Failed to sign in using email/password.", e)
            return EmailSignResult.UnknownError
        }
    }

    override suspend fun requestPasswordReset(email: String): EmailSignResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            EmailSignResult.Success
        } catch (e: FirebaseAuthInvalidUserException) {
            // User probably doesn't exist
            Timber.e("Failed to create account (InvalidUser): ${e.errorCode}", e)
            return EmailSignResult.InvalidUser
        } catch (e: Exception) {
            Timber.e("Failed to request password reset.", e)
            return EmailSignResult.UnknownError
        }
    }
}