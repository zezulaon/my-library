package dev.zezula.books.ui.screen.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.previewBooks
import dev.zezula.books.ui.screen.list.ImageThumbnail

@Composable
internal fun BookListItem(
    book: Book,
    onBookClick: (Book.Id) -> Unit,
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
                bookThumbnailUri = book.thumbnailLinkSecurityFix,
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
                if (book.userRating != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    StarRating(userRating = book.userRating, modifier = Modifier.height(16.dp))
                }
            }
        }
        if (isLast.not()) {
            Divider(thickness = 1.dp, modifier = Modifier.padding(start = 100.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookListItemPreview() {
    BookListItem(
        book = previewBooks.first(),
        onBookClick = {},
    )
}
