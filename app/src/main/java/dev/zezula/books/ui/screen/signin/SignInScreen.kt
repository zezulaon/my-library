package dev.zezula.books.ui.screen.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.ui.screen.about.AboutCard
import dev.zezula.books.ui.theme.MyLibraryTheme

@Composable
fun SignInRoute(
    viewModel: SignInViewModel,
    onSignInSuccess: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onEmailSignIn: () -> Unit,
    onContactClicked: () -> Unit,
    onReleaseNotesClicked: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isUserSignedIn) {
        val latestOnSignInSuccess by rememberUpdatedState(onSignInSuccess)
        LaunchedEffect(uiState) {
            latestOnSignInSuccess()
        }
    }

    uiState.errorMessage?.let { msg ->
        val text = stringResource(msg)
        LaunchedEffect(snackbarHostState, viewModel, msg, text) {
            snackbarHostState.showSnackbar(text)
            viewModel.snackbarMessageShown()
        }
    }

    SignInScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onGoogleSignInClick = {
            onGoogleSignIn()
            viewModel.onGoogleSignInClick()
        },
        onEmailSignInClick = onEmailSignIn,
        onAnonymousSignInClick = {
            viewModel.signInAnonymously()
        },
        onContactClicked = onContactClicked,
        onReleaseNotesClicked = onReleaseNotesClicked,
    )
}

@Composable
fun SignInScreen(
    uiState: SignInUiState,
    onGoogleSignInClick: () -> Unit,
    onEmailSignInClick: () -> Unit,
    onAnonymousSignInClick: () -> Unit,
    onContactClicked: () -> Unit,
    onReleaseNotesClicked: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            if (uiState.isSignInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    AboutCard(
                        onContactUsClicked = onContactClicked,
                        onReleaseNotesClicked = onReleaseNotesClicked,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            modifier = Modifier.width(220.dp),
                            onClick = onGoogleSignInClick,
                            enabled = uiState.isSignInProgress.not(),
                        ) {
                            Text(text = stringResource(id = R.string.btn_google_sign_in))
                        }
                        OutlinedButton(
                            modifier = Modifier.width(220.dp),
                            onClick = onEmailSignInClick,
                            enabled = uiState.isSignInProgress.not(),
                        ) {
                            Text(text = stringResource(id = R.string.btn_email_sign_in))
                        }
                        OutlinedButton(
                            modifier = Modifier.width(220.dp),
                            onClick = onAnonymousSignInClick,
                            enabled = uiState.isSignInProgress.not(),
                        ) {
                            Text(text = stringResource(id = R.string.btn_anonymous_sign_in))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyLibraryTheme {
        SignInScreen(
            uiState = SignInUiState(isSignInProgress = false),
            onGoogleSignInClick = {},
            onAnonymousSignInClick = {},
            onContactClicked = {},
            onReleaseNotesClicked = {},
            onEmailSignInClick = {},
        )
    }
}
