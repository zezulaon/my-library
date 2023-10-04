package dev.zezula.books.ui.screen.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.data.model.book.BookFormData
import dev.zezula.books.ui.screen.components.UserRatingComponent
import dev.zezula.books.ui.theme.MyLibraryTheme
import dev.zezula.books.util.createBookInputAuthor
import dev.zezula.books.util.createBookInputDesc
import dev.zezula.books.util.createBookInputIsbn
import dev.zezula.books.util.createBookInputPages
import dev.zezula.books.util.createBookInputPublisher
import dev.zezula.books.util.createBookInputTitle
import dev.zezula.books.util.createBookInputYear

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InputDataForm(
    uiState: CreateBookUiState,
    onTitleValueChanged: (String) -> Unit,
    onIsbnValueChanged: (String) -> Unit,
    onDescValueChanged: (String) -> Unit,
    onAuthorValueChanged: (String) -> Unit,
    onPublisherValueChanged: (String) -> Unit,
    onYearPublishedValueChanged: (String) -> Unit,
    onPageCountValueChanged: (String) -> Unit,
    onRatingStarSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val enabled = uiState.isInProgress.not()
    val bookFormData = uiState.bookFormData
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val invalidTitle = uiState.invalidForm && bookFormData.title.isNullOrEmpty()
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(createBookInputTitle),
            value = bookFormData.title.orEmpty(),
            isError = invalidTitle,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            enabled = enabled,
            onValueChange = { newValue -> onTitleValueChanged(newValue) },
            label = { Text(stringResource(R.string.create_book_label_title)) },
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(createBookInputAuthor),
            value = bookFormData.author.orEmpty(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            enabled = enabled,
            placeholder = { Text(stringResource(R.string.create_book_placeholder_author)) },
            onValueChange = { newValue -> onAuthorValueChanged(newValue) },
            label = { Text(stringResource(R.string.create_book_label_author)) },
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(createBookInputPublisher),
            value = bookFormData.publisher.orEmpty(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            enabled = enabled,
            onValueChange = { newValue -> onPublisherValueChanged(newValue) },
            label = { Text(stringResource(R.string.create_book_label_publisher)) },
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .testTag(createBookInputYear),
                value = bookFormData.yearPublished?.toString().orEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled,
                onValueChange = { newValue -> onYearPublishedValueChanged(newValue) },
                label = { Text(stringResource(R.string.create_book_label_year)) },
            )
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .testTag(createBookInputPages),
                value = bookFormData.pageCount?.toString().orEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled,
                onValueChange = { newValue -> onPageCountValueChanged(newValue) },
                label = { Text(stringResource(R.string.create_book_label_no_pages)) },
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(createBookInputIsbn),
            value = bookFormData.isbn.orEmpty(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Number,
            ),
            enabled = enabled,
            onValueChange = { newValue -> onIsbnValueChanged(newValue) },
            label = { Text(stringResource(R.string.create_book_label_isbn)) },
        )

        UserRatingComponent(
            userRating = uiState.bookFormData.userRating,
            onRatingStarSelected = onRatingStarSelected,
            modifier = Modifier
                .padding(top = 16.dp),
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 300.dp)
                .testTag(createBookInputDesc),
            value = bookFormData.description.orEmpty(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            enabled = enabled,
            onValueChange = { newValue -> onDescValueChanged(newValue) },
            label = { Text(stringResource(R.string.create_book_label_desc)) },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FormPreview() {
    MyLibraryTheme {
        InputDataForm(
            uiState = CreateBookUiState(
                BookFormData(
                    title = "Hobit",
                    author = "J. R. R. Tolkien",
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing e aliquip ex ea commodo consuat.",
                ),
            ),
            onTitleValueChanged = {},
            onDescValueChanged = {},
            onIsbnValueChanged = {},
            onAuthorValueChanged = {},
            onPublisherValueChanged = {},
            onPageCountValueChanged = {},
            onYearPublishedValueChanged = {},
            onRatingStarSelected = {},
        )
    }
}
