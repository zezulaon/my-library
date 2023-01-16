package dev.zezula.books.util

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import dev.zezula.books.BuildConfig

fun getGoogleSignInRequest(): BeginSignInRequest = BeginSignInRequest.builder()
    .setGoogleIdTokenRequestOptions(
        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
            // Set to true to return Google ID token (which is used for Firebase sign in).
            .setSupported(true)
            // Firebase/google cloud client ID.
            .setServerClientId(BuildConfig.ML_FIREBASE_CLIENT_ID)
            // Show all available accounts.
            .setFilterByAuthorizedAccounts(false)
            .build()
    )
    .build()