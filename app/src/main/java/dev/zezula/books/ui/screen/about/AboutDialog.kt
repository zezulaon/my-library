package dev.zezula.books.ui.screen.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.zezula.books.BuildConfig
import dev.zezula.books.R
import dev.zezula.books.ui.theme.MyLibraryTheme
import dev.zezula.books.util.shortUserId

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AboutDialog(
    onDismissRequested: () -> Unit,
    onContactUsClicked: () -> Unit,
    onReleaseNotesClicked: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequested,
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            AboutContent(onReleaseNotesClicked, onDismissRequested, onContactUsClicked)
        }
    }
}

@Composable
fun AboutCard(
    onContactUsClicked: () -> Unit,
    onReleaseNotesClicked: () -> Unit,
) {
    Card {
        AboutContent(
            onReleaseNotesClicked = onReleaseNotesClicked,
            onDismissRequested = {},
            onContactUsClicked = onContactUsClicked,
        )
    }
}

@Composable
private fun AboutContent(
    onReleaseNotesClicked: () -> Unit,
    onDismissRequested: () -> Unit,
    onContactUsClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}, ${shortUserId()})",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(top = 0.dp),
            text = stringResource(R.string.about_changes_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(top = 0.dp),
            text = stringResource(R.string.about_changes_desc),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.align(Alignment.End)) {
            TextButton(
                onClick = {
                    onReleaseNotesClicked()
                    onDismissRequested()
                },
            ) {
                Text(stringResource(R.string.about_btn_release_notes))
            }
            TextButton(
                onClick = {
                    onContactUsClicked()
                    onDismissRequested()
                },
            ) {
                Text(stringResource(R.string.about_btn_contact))
            }
        }
    }
}

@Composable
@Preview
fun PreviewAboutDialog() {
    MyLibraryTheme {
        AboutDialog({}, {}, {})
    }
}

@Composable
@Preview
fun PreviewAboutCard() {
    MyLibraryTheme {
        AboutCard({}, {})
    }
}
