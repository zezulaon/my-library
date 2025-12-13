package dev.zezula.books.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.zezula.books.R
import dev.zezula.books.core.model.Note

@Composable
internal fun AddOrEditNoteDialog(
    onDialogDismiss: () -> Unit,
    onDialogSaveClick: (text: String) -> Unit,
    onDialogUpdateClick: (note: Note, text: String) -> Unit,
    selectedNote: Note? = null,
) {
    val isInEditMode = selectedNote != null
    Dialog(onDismissRequest = onDialogDismiss) {
        Surface(
            shape = AlertDialogDefaults.shape,
            contentColor = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            val textValue = rememberSaveable { mutableStateOf(selectedNote?.text ?: "") }
            Column(modifier = Modifier.padding(all = 24.dp)) {
                val dialogTitleRes = if (isInEditMode) {
                    R.string.detail_title_edit_note
                } else {
                    R.string.detail_title_add_note
                }
                Text(
                    text = stringResource(dialogTitleRes),
                    style = MaterialTheme.typography.headlineSmall,
                    color = AlertDialogDefaults.titleContentColor,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 24.dp)
                        .heightIn(min = 100.dp, max = 500.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = textValue.value,
                    onValueChange = { textValue.value = it },
                )
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDialogDismiss) {
                        Text(stringResource(R.string.detail_notes_dialog_btn_cancel))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        enabled = textValue.value.isEmpty().not(),
                        onClick = {
                            if (isInEditMode) {
                                onDialogUpdateClick(selectedNote, textValue.value)
                            } else {
                                onDialogSaveClick(textValue.value)
                            }
                        },
                    ) {
                        val saveBtnRes = if (isInEditMode) {
                            R.string.detail_notes_dialog_btn_update
                        } else {
                            R.string.detail_notes_dialog_btn_save
                        }
                        Text(stringResource(saveBtnRes))
                    }
                }
            }
        }
    }
}
