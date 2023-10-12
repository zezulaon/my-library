package dev.zezula.books.ui.screen.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.util.homeNavDrawer

@Composable
internal fun NavigationDrawer(
    uiState: BookListUiState,
    onManageShelvesClick: () -> Unit,
    onAllBooksClick: () -> Unit,
    onAllAuthorsClick: () -> Unit,
    onShelfClick: (shelf: Shelf) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(modifier = Modifier.testTag(homeNavDrawer)) {
        Text(
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
            text = stringResource(R.string.drawer_app_title),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(12.dp))
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.drawer_item_manage_shelves)) },
            icon = {
                Icon(painter = painterResource(id = R.drawable.ic_shelves), contentDescription = null)
            },
            selected = false,
            onClick = onManageShelvesClick,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.drawer_item_all_authors)) },
            icon = {
                Icon(painter = painterResource(id = R.drawable.ic_all_authors), contentDescription = null)
            },
            selected = false,
            onClick = onAllAuthorsClick,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.drawer_item_all_books)) },
            icon = {
                Icon(painter = painterResource(id = R.drawable.ic_all_books), contentDescription = null)
            },
            selected = uiState.selectedShelf == null,
            onClick = onAllBooksClick,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
        Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 28.dp), thickness = 1.dp)
        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp),
            text = stringResource(R.string.drawer_label_shelves),
            style = MaterialTheme.typography.labelLarge,
        )
        // Add scrollable Column
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            uiState.drawerNavigation.shelves.forEach { shelf ->
                NavigationDrawerItem(
                    label = { Text(shelf.title) },
                    selected = shelf.id == uiState.selectedShelf?.id,
                    badge = {
                        Text(text = shelf.numberOfBooks.toString(), style = MaterialTheme.typography.labelLarge)
                    },
                    onClick = { onShelfClick(shelf) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
            }
        }
    }
}
