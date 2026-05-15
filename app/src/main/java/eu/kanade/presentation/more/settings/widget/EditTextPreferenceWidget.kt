package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import androidx.compose.material3.ButtonDefaults as MaterialButtonDefaults

@Composable
fun EditTextPreferenceWidget(
    title: String,
    subtitle: String?,
    dialogSubtitle: String? = null,
    icon: ImageVector?,
    value: String,
    onConfirm: suspend (String) -> Boolean,
    singleLine: Boolean = true,
    canBeBlank: Boolean = false,
    formatSubtitle: Boolean = true,
    validate: (String) -> Boolean = { true },
    errorMessage: @Composable ((String) -> String)? = null,
) {
    var isDialogShown by remember { mutableStateOf(false) }

    TextPreferenceWidget(
        title = title,
        subtitle = if (formatSubtitle) subtitle?.format(value) else subtitle,
        icon = icon,
        onPreferenceClick = { isDialogShown = true },
    )

    if (isDialogShown) {
        val scope = rememberCoroutineScope()
        val onDismissRequest = { isDialogShown = false }
        var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(value))
        }
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Column {
                    Text(text = title)
                    if (dialogSubtitle != null) {
                        Text(text = dialogSubtitle, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            text = {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    trailingIcon = {
                        if ((textFieldValue.text.isBlank() && !canBeBlank) || !validate(textFieldValue.text)) {
                            Icon(imageVector = Icons.Filled.Error, contentDescription = null)
                        } else {
                            IconButton(onClick = { textFieldValue = TextFieldValue("") }) {
                                Icon(imageVector = Icons.Filled.Cancel, contentDescription = null)
                            }
                        }
                    },
                    supportingText = {
                        if (!validate(textFieldValue.text) && errorMessage != null) {
                            Text(errorMessage(textFieldValue.text))
                        }
                    },
                    isError = (textFieldValue.text.isBlank() && !canBeBlank) || !validate(textFieldValue.text),
                    singleLine = singleLine,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.secondary,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = true,
            ),
            confirmButton = {
                TextButton(
                    enabled =
                    textFieldValue.text != value &&
                        (textFieldValue.text.isNotBlank() || canBeBlank) &&
                        validate(textFieldValue.text),
                    colors = MaterialButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    ),
                    onClick = {
                        scope.launch {
                            if (onConfirm(textFieldValue.text)) {
                                onDismissRequest()
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest,
                    colors = MaterialButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Text(text = stringResource(MR.strings.action_cancel))
                }
            },
        )
    }
}
