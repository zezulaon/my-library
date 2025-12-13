package dev.zezula.books.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.core.model.ShelfForBook
import dev.zezula.books.testtag.BookDetailTestTag

@Composable
fun TabShelves(
    uiState: BookDetailUiState,
    onShelfCheckedChange: (shelfForBook: ShelfForBook, isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.shelves.isEmpty()) {
        NoShelvesInfoCard(modifier = modifier.padding(24.dp))
    } else {
        ShelvesList(modifier, uiState, onShelfCheckedChange)
    }
}

@Composable
private fun ShelvesList(
    modifier: Modifier,
    uiState: BookDetailUiState,
    onShelfCheckedChange: (shelfForBook: ShelfForBook, isChecked: Boolean) -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth(),
    ) {
        LazyColumn(modifier = modifier.padding(16.dp)) {
            items(items = uiState.shelves, key = { shelf -> shelf.id.value }) { shelfForBook ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = shelfForBook.title)
                    Spacer(modifier = modifier.weight(weight = 1f))
                    Checkbox(
                        modifier = Modifier.testTag(BookDetailTestTag.checkboxShelf(shelfForBook.title)),
                        checked = shelfForBook.isBookAdded,
                        onCheckedChange = { onShelfCheckedChange(shelfForBook, it) },
                    )
                }
            }
        }
    }
}

@Composable
fun NoShelvesInfoCard(
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = stringResource(R.string.detail_shelves_empty_info_card_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = stringResource(R.string.detail_shelves_empty_info_card_content),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
