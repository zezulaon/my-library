package dev.zezula.books.ui.screen.shelves

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.util.manageShelvesBtnExpand

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun ShelfListItem(
    shelf: Shelf,
    onEditShelfClick: (Shelf) -> Unit,
    onDeleteClick: (Shelf) -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isExpanded) 8.dp else 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ListItem(
                headlineContent = { Text(text = shelf.title) },
                supportingContent = {
                    Text(
                        text = pluralStringResource(
                            R.plurals.shelves_label_books_count,
                            shelf.numberOfBooks,
                            shelf.numberOfBooks,
                        ),
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = onExpandClick,
                        modifier = Modifier.testTag(manageShelvesBtnExpand),
                    ) {
                        val expandIonRes = if (isExpanded) {
                            R.drawable.ic_shelf_item_expand_less
                        } else {
                            R.drawable.ic_shelf_item_expand_more
                        }
                        Icon(
                            painter = painterResource(id = expandIonRes),
                            contentDescription = null,
                        )
                    }
                },
            )

            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .padding(end = 24.dp, bottom = 16.dp)
                        .align(End),
                ) {
                    TextButton(onClick = { onDeleteClick(shelf) }) {
                        Text(stringResource(R.string.shelves_btn_remove))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onEditShelfClick(shelf) }) {
                        Text(stringResource(R.string.shelves_btn_edit))
                    }
                }
            }
        }
    }
}
