package dev.zezula.books.ui.screen.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.util.detailShelfCheckbox

@Composable
fun TabShelves(
    uiState: BookDetailUiState,
    onShelfCheckedChange: (shelfForBook: ShelfForBook, isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        LazyColumn(modifier = modifier.padding(16.dp)) {
            items(items = uiState.shelves, key = { shelf -> shelf.id }) { shelfForBook ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = shelfForBook.title)
                    Spacer(modifier = modifier.weight(weight = 1f))
                    Checkbox(
                        modifier = Modifier.testTag(detailShelfCheckbox),
                        checked = shelfForBook.isBookAdded,
                        onCheckedChange = { onShelfCheckedChange(shelfForBook, it) }
                    )
                }
            }
        }
    }
}