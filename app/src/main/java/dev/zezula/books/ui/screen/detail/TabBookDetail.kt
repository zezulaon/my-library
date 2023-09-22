package dev.zezula.books.ui.screen.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.ui.screen.components.UserRatingComponent
import dev.zezula.books.ui.screen.list.ImageThumbnail
import dev.zezula.books.ui.theme.MyLibraryTheme

@Composable
fun TabBookDetail(
    uiState: BookDetailUiState,
    modifier: Modifier = Modifier,
    onAmazonLinkClicked: (book: Book) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
        ) {
            Column {
                Row {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        ImageThumbnail(
                            modifier = Modifier
                                .fillMaxSize()
                                .defaultMinSize(minHeight = 270.dp),
                            bookThumbnailUri = uiState.book?.thumbnailLink,
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BookCoverItem(
                            value = uiState.book?.pageCount?.toString() ?: "",
                            labelRes = R.string.detail_label_page_count,
                        )
                        BookCoverItem(
                            value = uiState.book?.publisher ?: "",
                            labelRes = R.string.detail_label_publisher,
                        )
                        BookCoverItem(
                            value = uiState.book?.yearPublished?.toString() ?: "",
                            labelRes = R.string.detail_label_year_published,
                        )
                        BookCoverItem(
                            value = uiState.book?.isbn ?: "",
                            labelRes = R.string.detail_label_year_isbn,
                        )
                        BookCoverItem(
                            value = uiState.book?.dateAddedFormatted ?: "",
                            labelRes = R.string.detail_label_year_added_on,
                        )
                    }
                }
            }
        }

        uiState.book?.userRating?.let { rating ->
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
            ) {
                UserRatingComponent(userRating = rating, modifier = Modifier.padding(16.dp))
            }
        }

        uiState.book?.description?.let { description ->
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = description)
                }
            }
        }
        val title = uiState.book?.title
        if (title != null) {
            Links(onAmazonLinkClicked = {
                onAmazonLinkClicked(uiState.book)
            })
        }
    }
}

@Composable
private fun Links(
    onAmazonLinkClicked: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.detail_links_title), style = MaterialTheme.typography.titleMedium)
        }
        Divider(
            modifier = Modifier
                .padding(vertical = 0.dp)
                .fillMaxWidth(),
            thickness = .5.dp,
        )
        ListItem(
            modifier = Modifier.clickable {
                onAmazonLinkClicked()
            },
            headlineContent = {
                Text(text = stringResource(R.string.detail_link_amazon), style = MaterialTheme.typography.bodyMedium)
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
private fun BookCoverItem(
    value: String,
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = stringResource(id = labelRes), style = MaterialTheme.typography.labelLarge)
        Text(text = value)
    }
}

@Preview(showBackground = true)
@Composable
private fun TabDetailPreview() {
    MyLibraryTheme {
        TabBookDetail(
            uiState = BookDetailUiState(
                selectedTab = DetailTab.Detail,
                shelves = listOf(ShelfForBook("1", "Test", true)),
                isBookDeleted = false,
                book = Book(
                    id = "id",
                    title = "Title",
                    author = "Tolkien",
                    description = "Desc",
                    isbn = "545454",
                    publisher = "",
                    yearPublished = 1989,
                    pageCount = 565,
                    thumbnailLink = null,
                    userRating = 5,
                    dateAdded = "2023-01-05T17:43:25.629",
                ),
            ),
        )
    }
}
