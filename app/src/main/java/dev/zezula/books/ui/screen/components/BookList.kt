package dev.zezula.books.ui.screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.util.isLastIndex

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookList(
    books: List<Book>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    animateItemChanges: Boolean = true,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(key = { _, item -> item.id }, items = books) { index, book ->
            val isLast = books.isLastIndex(index)
            val itemModifier = if (animateItemChanges) {
                Modifier.animateItemPlacement()
            } else {
                Modifier
            }
            BookListItem(
                modifier = itemModifier,
                book = book,
                onBookClick = onBookClick,
                isLast = isLast,
            )
        }
    }
}
