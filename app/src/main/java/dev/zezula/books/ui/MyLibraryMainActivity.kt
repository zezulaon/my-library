package dev.zezula.books.ui

import android.content.IntentSender
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import dev.zezula.books.ui.screen.signin.SignInViewModel
import dev.zezula.books.util.getGoogleSignInRequest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MyLibraryMainActivity : ComponentActivity() {

    private val userViewModel: SignInViewModel by viewModel()

    // TODO: This could be replaced with rememberLauncherForActivityResult()
    private val googleSignInResultLauncher = registerForActivityResult(StartIntentSenderForResult()) { activityResult ->
        onSignInResult(activityResult)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate($savedInstanceState)")

        lifecycleScope.launch {
            userViewModel.updateLastSignedInDate()
        }

        setContent {
            MyLibraryUiApp(getStartDestination())
        }
    }

    private fun getStartDestination(): String {
        return if (userViewModel.isSignedIn()) {
            Destinations.bookListRoute
        } else {
            Destinations.signInRoute
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy()")
    }

    internal fun beginGoogleSignIn() {
        Timber.d("beginGoogleSignIn()")

        try {
            val oneTapClient = Identity.getSignInClient(this)
            oneTapClient.beginSignIn(getGoogleSignInRequest()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.d("beginSignIn() completed successfully -> launching Google Sign In UI")
                    val intentSender = it.result.pendingIntent.intentSender
                    googleSignInResultLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                } else {
                    Timber.e(it.exception, "Google Sign In unsuccessful")
                    userViewModel.onGoogleSignInFailed()
                }
            }
        } catch (e: ApiException) {
            Timber.e(e, "Failed to begin Google Sign In")
            userViewModel.onGoogleSignInFailed()
        } catch (e: IntentSender.SendIntentException) {
            Timber.e(e, "Failed to begin Google Sign In")
            userViewModel.onGoogleSignInFailed()
        }
    }

    private fun onSignInResult(activityResult: ActivityResult) {
        Timber.d("onSignInResult() isSuccess: ${activityResult.resultCode == RESULT_OK}")

        val resultIntent = activityResult.data
        if (activityResult.resultCode == RESULT_OK && resultIntent != null) {
            val oneTapClient = Identity.getSignInClient(this)
            try {
                val googleCredential = oneTapClient.getSignInCredentialFromIntent(resultIntent)
                val idToken = googleCredential.googleIdToken
                Timber.d("Received google ID token: $idToken")
                idToken?.let { userViewModel.googleSignIn(it) }
            } catch (e: ApiException) {
                Timber.e(e, "Failed to get google ID token")
            }
        } else {
            Timber.e("Failed to get google ID token")
            userViewModel.onGoogleSignInFailed()
        }
    }
}
