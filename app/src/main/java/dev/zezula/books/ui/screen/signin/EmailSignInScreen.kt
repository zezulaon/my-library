package dev.zezula.books.ui.screen.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.ui.theme.MyLibraryTheme

@Composable
fun EmailSignInRoute(
    viewModel: EmailSignInViewModel,
    onSignInSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isUserSignedIn) {
        val latestOnSignInSuccess by rememberUpdatedState(onSignInSuccess)
        LaunchedEffect(uiState) {
            latestOnSignInSuccess()
        }
    }

    uiState.uiMessage?.let { msg ->
        val text = stringResource(msg)
        LaunchedEffect(snackbarHostState, viewModel, msg, text) {
            snackbarHostState.showSnackbar(text)
            viewModel.snackbarMessageShown()
        }
    }

    EmailSignInScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onSignInClicked = viewModel::onSignInClicked,
        onRegisterClicked = viewModel::onRegisterClicked,
        onCreateAccountClicked = viewModel::onCreateAccountClicked,
        onRequestPasswordResetClicked = viewModel::onRequestPasswordResetClicked,
        onNavigateBack = {
            if (viewModel.onBackClicked().not()) {
                onNavigateBack()
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailSignInScreen(
    uiState: EmailSignInUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onSignInClicked: () -> Unit = {},
    onRegisterClicked: () -> Unit = {},
    onCreateAccountClicked: () -> Unit = {},
    onRequestPasswordResetClicked: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            val titleRes = if (uiState.mode == EmailSignMode.SignIn) {
                R.string.screen_title_email_sign_in
            } else {
                R.string.screen_title_email_register
            }
            TopAppBar(
                title = { Text(stringResource(titleRes)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_navigate_back),
                        )
                    }
                },
            )
        },
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

            SignInForm(
                uiState = uiState,
                onEmailChanged = onEmailChanged,
                onPasswordChanged = onPasswordChanged,
                onSignInClicked = onSignInClicked,
                onRegisterClicked = onRegisterClicked,
                onCreateAccountClicked = onCreateAccountClicked,
                onRequestPasswordResetClicked = onRequestPasswordResetClicked,
            )
        }
    }
}

@Composable
private fun SignInForm(
    uiState: EmailSignInUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignInClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
    onCreateAccountClicked: () -> Unit,
    onRequestPasswordResetClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = uiState.email,
            enabled = uiState.areEditTextsEnabled,
            isError = uiState.invalidEmail,
            supportingText = {
                if (uiState.invalidEmail) {
                    Text(stringResource(R.string.email_sign_in_label_invalid_email))
                }
            },
            onValueChange = { newValue -> onEmailChanged(newValue) },
            label = { Text(stringResource(R.string.sign_in_email_label)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            enabled = uiState.areEditTextsEnabled,
            isError = uiState.invalidPassword,
            supportingText = {
                if (uiState.invalidPassword) {
                    Text(stringResource(R.string.email_sign_in_label_invalid_password))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { newValue -> onPasswordChanged(newValue) },
            label = { Text(stringResource(R.string.sign_in_password_label)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.mode == EmailSignMode.SignIn) {
            Button(onClick = onSignInClicked, enabled = uiState.isSignInButtonEnabled) {
                Text(stringResource(R.string.sign_in_button_text))
            }
        } else {
            Button(onClick = onRegisterClicked, enabled = uiState.isSignInButtonEnabled) {
                Text(stringResource(R.string.sign_in_button_register))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.mode == EmailSignMode.SignIn) {
            Text(stringResource(R.string.sign_in_label_or), style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = onCreateAccountClicked, enabled = uiState.isSignInProgress.not()) {
                    Text(stringResource(R.string.create_account_button_text))
                }

                TextButton(onClick = onRequestPasswordResetClicked, enabled = uiState.isSignInProgress.not()) {
                    Text(text = stringResource(R.string.request_new_password_button_text))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailSignInPreview() {
    MyLibraryTheme {
        EmailSignInScreen(
            uiState = EmailSignInUiState(
                email = "test@email.com",
            ),
        )
    }
}
