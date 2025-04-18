package dev.zezula.books.ui.screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
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
    onBookClick: (Book.Id) -> Unit,
    modifier: Modifier = Modifier,
    animateItemChanges: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(key = { _, item -> item.id.value }, items = books) { index, book ->
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
