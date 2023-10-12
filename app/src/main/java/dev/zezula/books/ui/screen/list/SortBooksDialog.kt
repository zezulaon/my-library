package dev.zezula.books.ui.screen.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.data.SortBooksBy

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SortBooksDialog(
    uiState: BookListUiState,
    onSortSelected: (SortBooksBy) -> Unit,
    onDismissRequested: () -> Unit,
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
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.sort_books_by_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.selectableGroup()) {
                    val selectedSortBooksBy = uiState.sorting.sortBooksBy
                    SortBooksBy.entries
                        .filter { it != SortBooksBy.USER_RATING || uiState.sorting.canSortByRating }
                        .forEach { sortBooksBy ->
                            SortItem(
                                currentSort = sortBooksBy,
                                isSelected = sortBooksBy == selectedSortBooksBy,
                                onOptionsSelected = { onSortSelected(sortBooksBy) },
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun SortItem(currentSort: SortBooksBy, isSelected: Boolean, onOptionsSelected: () -> Unit) {
    val title = stringResource(currentSort.dialogTitleRes())
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = isSelected,
                onClick = { onOptionsSelected() },
                role = Role.RadioButton,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null, // null recommended for accessibility with screen readers
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

private fun SortBooksBy.dialogTitleRes(): Int {
    return when (this) {
        SortBooksBy.TITLE -> R.string.sort_books_by_label_title
        SortBooksBy.AUTHOR -> R.string.sort_books_by_label_author
        SortBooksBy.DATE_ADDED -> R.string.sort_books_by_label_date
        SortBooksBy.USER_RATING -> R.string.sort_books_by_label_rating
    }
}
