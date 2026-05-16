package eu.kanade.presentation.updates.anime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.EtsumeMediaListCard
import eu.kanade.presentation.components.relativeDateText
import eu.kanade.presentation.util.animateItemFastScroll
import eu.kanade.presentation.util.relativeTimeSpanString
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.anime.model.AnimeDownload
import eu.kanade.tachiyomi.ui.updates.anime.AnimeUpdatesItem
import tachiyomi.domain.updates.anime.model.AnimeUpdatesWithRelations
import tachiyomi.i18n.MR
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import java.util.concurrent.TimeUnit

internal fun LazyListScope.animeUpdatesLastUpdatedItem(
    lastUpdated: Long,
) {
    item(key = "animeUpdates-lastUpdated") {
        Surface(
            modifier = Modifier
                .animateItem(fadeInSpec = null, fadeOutSpec = null)
                .padding(
                    horizontal = MaterialTheme.padding.medium,
                    vertical = MaterialTheme.padding.small,
                ),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
            tonalElevation = 2.dp,
        ) {
            Text(
                text = stringResource(MR.strings.updates_last_update_info, relativeTimeSpanString(lastUpdated)),
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            )
        }
    }
}

internal fun LazyListScope.animeUpdatesUiItems(
    uiModels: List<AnimeUpdatesUiModel>,
    selectionMode: Boolean,
    onUpdateSelected: (AnimeUpdatesItem, Boolean, Boolean, Boolean) -> Unit,
    onClickCover: (AnimeUpdatesItem) -> Unit,
    onClickUpdate: (AnimeUpdatesItem, altPlayer: Boolean) -> Unit,
    onDownloadEpisode: (List<AnimeUpdatesItem>, eu.kanade.presentation.entries.anime.components.EpisodeDownloadAction) -> Unit,
) {
    items(
        items = uiModels,
        contentType = {
            when (it) {
                is AnimeUpdatesUiModel.Header -> "header"
                is AnimeUpdatesUiModel.Item -> "item"
            }
        },
        key = {
            when (it) {
                is AnimeUpdatesUiModel.Header -> "animeUpdatesHeader-${it.hashCode()}"
                is AnimeUpdatesUiModel.Item -> "animeUpdates-${it.item.update.animeId}-${it.item.update.episodeId}"
            }
        },
    ) { item ->
        when (item) {
            is AnimeUpdatesUiModel.Header -> {
                Surface(
                    modifier = Modifier
                        .animateItemFastScroll()
                        .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 0.dp,
                ) {
                    Text(
                        text = relativeDateText(item.date),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
            is AnimeUpdatesUiModel.Item -> {
                val updatesItem = item.item
                AnimeUpdatesUiItem(
                    modifier = Modifier.animateItemFastScroll(),
                    update = updatesItem.update,
                    selected = updatesItem.selected,
                    watchProgress = updatesItem.update.lastSecondSeen
                        .takeIf { !updatesItem.update.seen && it > 0L }
                        ?.let {
                            stringResource(
                                AYMR.strings.episode_progress,
                                formatProgress(it),
                                formatProgress(updatesItem.update.totalSeconds),
                            )
                        },
                    onLongClick = {
                        onUpdateSelected(updatesItem, !updatesItem.selected, true, true)
                    },
                    onClick = {
                        when {
                            selectionMode -> onUpdateSelected(
                                updatesItem,
                                !updatesItem.selected,
                                true,
                                false,
                            )
                            else -> onClickUpdate(updatesItem, false)
                        }
                    },
                    onClickCover = { onClickCover(updatesItem) }.takeIf { !selectionMode },
                    downloadStateProvider = updatesItem.downloadStateProvider,
                    downloadProgressProvider = updatesItem.downloadProgressProvider,
                )
            }
        }
    }
}

@Composable
private fun AnimeUpdatesUiItem(
    update: AnimeUpdatesWithRelations,
    selected: Boolean,
    watchProgress: String?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickCover: (() -> Unit)?,
    downloadStateProvider: () -> AnimeDownload.State,
    downloadProgressProvider: () -> Int,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val detail = buildString {
        append("New ")
        append(update.episodeName)
        if (watchProgress != null) {
            append(" • ")
            append(watchProgress)
        }
    }

    Box(modifier = modifier) {
        EtsumeMediaListCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            title = update.animeTitle,
            subtitle = detail,
            coverData = update.coverData,
            actionLabel = androidStringResource(R.string.etsume_action_open),
            onClick = onClick,
            onLongClick = {
                onLongClick()
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            onActionClick = onClickCover ?: onClick,
            isSelected = selected,
        )
    }
}

private fun formatProgress(milliseconds: Long): String {
    return if (milliseconds > 3600000L) {
        String.format(
            "%d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliseconds),
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)),
        )
    } else {
        String.format(
            "%d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)),
        )
    }
}
