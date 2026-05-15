package eu.kanade.presentation.track.manga

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.DropdownMenu
import eu.kanade.presentation.entries.components.ItemCover
import eu.kanade.presentation.theme.EtsumeAuroraBackdrop
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.presentation.track.components.EtsumeTrackCard
import eu.kanade.presentation.track.components.EtsumeTrackPill
import eu.kanade.tachiyomi.data.track.model.MangaTrackSearch
import eu.kanade.tachiyomi.util.system.openInBrowser
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen
import tachiyomi.presentation.core.util.plus
import tachiyomi.presentation.core.util.runOnEnterKeyPressed
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
fun MangaTrackerSearch(
    state: TextFieldState,
    onDispatchQuery: () -> Unit,
    queryResult: Result<List<MangaTrackSearch>>?,
    selected: MangaTrackSearch?,
    onSelectedChange: (MangaTrackSearch) -> Unit,
    onConfirmSelection: (private: Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    supportsPrivateTracking: Boolean,
) {
    val focusManager = LocalFocusManager.current
    val dispatchQueryAndClearFocus: () -> Unit = {
        onDispatchQuery()
        focusManager.clearFocus()
    }

    Box {
        EtsumeAuroraBackdrop()
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                    ),
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    title = {
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(etsumeGlassContainerColor(0.6f))
                                .border(
                                    width = 1.dp,
                                    color = etsumeGlassBorderColor(0.24f),
                                    shape = MaterialTheme.shapes.extraLarge,
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                        ) {
                            BasicTextField(
                                state = state,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .runOnEnterKeyPressed(action = dispatchQueryAndClearFocus),
                                textStyle = MaterialTheme.typography.bodyLarge
                                    .copy(color = MaterialTheme.colorScheme.onSurface),
                                lineLimits = TextFieldLineLimits.SingleLine,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                onKeyboardAction = { dispatchQueryAndClearFocus() },
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
                                decorator = {
                                    if (state.text.isEmpty()) {
                                        Text(
                                            text = stringResource(MR.strings.action_search_hint),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                    it()
                                },
                            )
                        }
                    },
                    actions = {
                        if (state.text.isNotEmpty()) {
                            IconButton(
                                onClick = { state.clearText() },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = selected != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = slideOutVertically { it / 2 } + fadeOut(),
                ) {
                    EtsumeTrackCard(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .windowInsetsPadding(WindowInsets.navigationBars),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Button(
                                onClick = { onConfirmSelection(false) },
                                modifier = Modifier.weight(1f),
                                elevation = ButtonDefaults.elevatedButtonElevation(),
                            ) {
                                Text(text = stringResource(MR.strings.action_track))
                            }
                            if (supportsPrivateTracking) {
                                Button(
                                    onClick = { onConfirmSelection(true) },
                                    elevation = ButtonDefaults.elevatedButtonElevation(),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.VisibilityOff,
                                        contentDescription = stringResource(MR.strings.action_toggle_private_on),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            if (queryResult == null) {
                LoadingScreen(modifier = Modifier.padding(innerPadding))
            } else {
                val availableTracks = queryResult.getOrNull()
                if (availableTracks != null) {
                    if (availableTracks.isEmpty()) {
                        EmptyScreen(
                            modifier = Modifier.padding(innerPadding),
                            stringRes = MR.strings.no_results_found,
                        )
                    } else {
                        ScrollbarLazyColumn(
                            contentPadding = innerPadding + PaddingValues(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(
                                items = availableTracks,
                                key = { it.hashCode() },
                            ) {
                                SearchResultItem(
                                    trackSearch = it,
                                    selected = it == selected,
                                    onClick = { onSelectedChange(it) },
                                )
                            }
                        }
                    }
                } else {
                    EmptyScreen(
                        modifier = Modifier.padding(innerPadding),
                        message = queryResult.exceptionOrNull()?.message
                            ?: stringResource(MR.strings.unknown_error),
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    trackSearch: MangaTrackSearch,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val type = trackSearch.publishing_type.toLowerCase(Locale.current).capitalize(Locale.current)
    val status = trackSearch.publishing_status.toLowerCase(Locale.current).capitalize(Locale.current)
    val description = trackSearch.summary.trim()
    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    EtsumeTrackCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .combinedClickable(
                onLongClick = { dropDownMenuExpanded = true },
                onClick = {
                    focusManager.clearFocus()
                    onClick()
                },
            )
            .padding(12.dp),
        selected = selected,
    ) {
        Column {
            Row {
                ItemCover.Book(
                    data = trackSearch.cover_url,
                    modifier = Modifier.height(96.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (type.isNotBlank()) {
                            EtsumeTrackPill(text = type, emphasized = selected)
                        }
                        if (status.isNotBlank()) {
                            EtsumeTrackPill(text = status)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = trackSearch.title,
                        modifier = Modifier.padding(end = 28.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    SearchResultItemDropDownMenu(
                        expanded = dropDownMenuExpanded,
                        onCollapseMenu = { dropDownMenuExpanded = false },
                        onCopyName = {
                            clipboardManager.setText(AnnotatedString(trackSearch.title))
                        },
                        onOpenInBrowser = {
                            val url = trackSearch.tracking_url
                            if (url.isNotBlank()) context.openInBrowser(url)
                        },
                    )
                    if (trackSearch.authors.isNotEmpty() || trackSearch.artists.isNotEmpty()) {
                        Text(
                            text = (trackSearch.authors + trackSearch.artists).distinct().joinToString(),
                            modifier = Modifier.secondaryItemAlpha(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    if (trackSearch.start_date.isNotBlank()) {
                        SearchResultItemDetails(
                            title = stringResource(MR.strings.label_started),
                            text = trackSearch.start_date,
                        )
                    }
                    if (trackSearch.score != -1.0) {
                        SearchResultItemDetails(
                            title = stringResource(MR.strings.score),
                            text = trackSearch.score.toString(),
                        )
                    }
                }
            }
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    modifier = Modifier
                        .paddingFromBaseline(top = 24.dp)
                        .secondaryItemAlpha(),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        if (selected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun SearchResultItemDropDownMenu(
    expanded: Boolean,
    onCollapseMenu: () -> Unit,
    onCopyName: () -> Unit,
    onOpenInBrowser: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onCollapseMenu,
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(MR.strings.action_copy_to_clipboard)) },
            onClick = {
                onCopyName()
                onCollapseMenu()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(MR.strings.action_open_in_browser)) },
            onClick = {
                onOpenInBrowser()
                onCollapseMenu()
            },
        )
    }
}

@Composable
private fun SearchResultItemDetails(
    title: String,
    text: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall)) {
        Text(
            text = title,
            maxLines = 1,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .secondaryItemAlpha(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@PreviewLightDark
@Composable
private fun TrackerSearchPreviews(
    @PreviewParameter(MangaTrackerSearchPreviewProvider::class)
    content: @Composable () -> Unit,
) {
    TachiyomiPreviewTheme {
        Surface {
            content()
        }
    }
}
