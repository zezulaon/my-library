package dev.zezula.books.ui.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.zezula.books.data.model.book.Book

@Composable
internal fun BookListItem(
    book: Book,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
) {
    Column(
        modifier = modifier.clickable { onBookClick(book.id) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ImageThumbnail(
                modifier = Modifier
                    .width(64.dp)
                    .height(90.dp),
                bookThumbnailUri = book.thumbnailLink,
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(weight = 1f, fill = true)) {
                Text(
                    text = book.title.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
                Text(text = book.author ?: "", style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (isLast.not()) {
            Divider(thickness = 1.dp, modifier = Modifier.padding(start = 100.dp))
        }
    }
}
