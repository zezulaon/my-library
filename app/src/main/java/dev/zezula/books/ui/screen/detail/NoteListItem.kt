package dev.zezula.books.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.core.model.Note
import dev.zezula.books.core.utils.test.manageShelvesBtnExpand

@Composable
internal fun NoteListItem(
    note: Note,
    onEditClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    OutlinedCard(
        modifier = modifier.padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Row() {
                    Column {
                        Text(text = note.dateAddedFormatted, style = MaterialTheme.typography.labelLarge)
                        if (note.page != null) {
                            val page = "Page: ${note.page}"
                            Text(text = page, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (note.type == "quote") {
                        Icon(
                            modifier = Modifier
                                .size(42.dp),
                            painter = painterResource(id = R.drawable.ic_quote),
                            contentDescription = null,
                        )
                    }
                }
                Text(text = note.text, style = MaterialTheme.typography.bodyMedium)
                IconButton(
                    onClick = onExpandClick,
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag(manageShelvesBtnExpand),
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
            }
            if (isExpanded) {
                Divider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    thickness = .5.dp,
                )
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                        .align(Alignment.End),
                ) {
                    TextButton(onClick = { onDeleteClick(note) }) {
                        Text(stringResource(R.string.detail_notes_btn_remove))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onEditClick(note) }) {
                        Text(stringResource(R.string.detail_notes_btn_edit))
                    }
                }
            }
        }
    }
}
