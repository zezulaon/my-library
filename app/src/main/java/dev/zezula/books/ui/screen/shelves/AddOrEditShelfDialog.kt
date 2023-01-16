package dev.zezula.books.ui.screen.shelves

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.zezula.books.R
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.util.createShelfInputTitle

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AddOrEditShelfDialog(
    onDialogDismiss: () -> Unit,
    onDialogSaveClick: (shelfTitle: String) -> Unit,
    onDialogUpdateClick: (shelf: Shelf, updatedShelfTitle: String) -> Unit,
    selectedShelf: Shelf? = null,
) {
    val isInEditMode = selectedShelf != null
    Dialog(onDismissRequest = onDialogDismiss) {
        Surface(
            shape = AlertDialogDefaults.shape,
            contentColor = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {

            val textValue = rememberSaveable { mutableStateOf(selectedShelf?.title ?: "") }
            Column(modifier = Modifier.padding(all = 24.dp)) {
                val dialogTitleRes = if (isInEditMode) {
                    R.string.shelves_dialog_title_edit
                } else {
                    R.string.shelves_dialog_title_add
                }
                Text(
                    text = stringResource(dialogTitleRes),
                    style = MaterialTheme.typography.headlineSmall,
                    color = AlertDialogDefaults.titleContentColor
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 24.dp)
                        .testTag(createShelfInputTitle),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = textValue.value,
                    onValueChange = { textValue.value = it },
                )
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDialogDismiss) {
                        Text(stringResource(R.string.shelves_dialog_btn_cancel))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        enabled = textValue.value.isEmpty().not(),
                        onClick = {
                            if (isInEditMode) {
                                selectedShelf?.let {
                                    onDialogUpdateClick(it, textValue.value)
                                }
                            } else {
                                onDialogSaveClick(textValue.value)
                            }
                        }
                    ) {
                        val saveBtnRes = if (isInEditMode) {
                            R.string.shelves_dialog_btn_update
                        } else {
                            R.string.shelves_dialog_btn_save
                        }
                        Text(stringResource(saveBtnRes))
                    }
                }
            }
        }
    }
}