package dev.zezula.books.ui.screen.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.ui.theme.MyLibraryTheme

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SignInRoute(
    viewModel: SignInViewModel,
    onSignInSuccess: () -> Unit,
    onGoogleSignIn: () -> Unit,
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
        onAnonymousSignInClick = {
            viewModel.signInAnonymously()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    uiState: SignInUiState,
    onGoogleSignInClick: () -> Unit,
    onAnonymousSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            if (uiState.isSignInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(300.dp),
                        painter = painterResource(id = R.drawable.image_logo),
                        contentDescription = null
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
            onAnonymousSignInClick = {}
        )
    }
}
