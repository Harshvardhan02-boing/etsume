package eu.kanade.presentation.reader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.browse.BrowseActionChip
import eu.kanade.presentation.components.EtsumeToolbarActionButton
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.reader.ReaderViewModel
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ReaderChapterDrawer(
    visible: Boolean,
    chapters: List<ReaderViewModel.ChapterDrawerItem>,
    onDismissRequest: () -> Unit,
    onOpenChapter: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val currentIndex = chapters.indexOfFirst { it.isCurrent }.coerceAtLeast(0)

    LaunchedEffect(visible, currentIndex) {
        if (visible && chapters.isNotEmpty()) {
            listState.scrollToItem((currentIndex - 1).coerceAtLeast(0))
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it }),
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.42f))
                    .clickable(onClick = onDismissRequest),
            )

            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.88f)
                    .widthIn(max = 380.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
                contentColor = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                border = BorderStroke(1.dp, etsumeGlassBorderColor(0.26f)),
                tonalElevation = 10.dp,
                shadowElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = androidStringResource(R.string.etsume_reader_chapters_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "${chapters.size} ${stringResource(MR.strings.chapters).lowercase()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        EtsumeToolbarActionButton(
                            onClick = onDismissRequest,
                            contentDescription = stringResource(MR.strings.action_close),
                            modifier = Modifier.size(40.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null,
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(chapters, key = { it.id }) { chapter ->
                            ReaderChapterCard(
                                item = chapter,
                                onClick = { onOpenChapter(chapter.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderChapterCard(
    item: ReaderViewModel.ChapterDrawerItem,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = if (item.isCurrent) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.92f)
        } else {
            etsumeGlassContainerColor(0.52f)
        },
        contentColor = if (item.isCurrent) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        border = BorderStroke(
            1.dp,
            if (item.isCurrent) {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.30f)
            } else {
                etsumeGlassBorderColor(0.24f)
            },
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (item.isCurrent) {
                    Text(
                        text = androidStringResource(R.string.etsume_reader_current_badge),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = when {
                        item.isCompleted -> androidStringResource(R.string.etsume_reader_completed)
                        item.resumePage != null && item.exactTotalPages != null -> {
                            androidStringResource(
                                R.string.etsume_reader_page_progress,
                                item.resumePage,
                                item.exactTotalPages,
                            )
                        }
                        item.resumePage != null -> {
                            androidStringResource(
                                R.string.etsume_reader_resume_page,
                                item.resumePage,
                            )
                        }
                        else -> androidStringResource(R.string.etsume_reader_start_reading)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                item.progressFraction?.let { progress ->
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    )
                }
            }

            BrowseActionChip(
                label = androidStringResource(
                    if (item.shouldContinue) R.string.etsume_action_continue else R.string.etsume_action_open,
                ),
                onClick = onClick,
            )
        }
    }
}
