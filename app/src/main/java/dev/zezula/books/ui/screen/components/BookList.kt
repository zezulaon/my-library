package dev.zezula.books.ui.screen.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.util.isLastIndex

@Composable
fun BookList(
    books: List<Book>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(key = { _, item -> item.id }, items = books) { index, book ->
            val isLast = books.isLastIndex(index)
            BookListItem(book = book, onBookClick = onBookClick, isLast = isLast)
        }
    }
}
