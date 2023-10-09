package dev.zezula.books.util

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import dev.zezula.books.BuildConfig
import timber.log.Timber
import java.lang.IllegalStateException

fun getGoogleSignInRequest(): BeginSignInRequest = BeginSignInRequest.builder()
    .setGoogleIdTokenRequestOptions(
        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
            // Set to true to return Google ID token (which is used for Firebase sign in).
            .setSupported(true)
            // Firebase/google cloud client ID.
            .setServerClientId(BuildConfig.ML_FIREBASE_CLIENT_ID)
            // Show all available accounts.
            .setFilterByAuthorizedAccounts(false)
            .build(),
    )
    .build()

fun shortUserId(): String {
    val id = try {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.substring(0, 4)
    } catch (e: IllegalStateException) {
        Timber.e(e)
        null
    }
    return id ?: "n/a"
}
