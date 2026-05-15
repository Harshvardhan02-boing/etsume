package eu.kanade.presentation.reader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.comments.EtsumeCommentPlaceholder
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.database.models.manga.toDomainChapter
import eu.kanade.tachiyomi.ui.reader.model.ChapterTransition as ReaderChapterTransition
import tachiyomi.domain.items.chapter.model.Chapter
import tachiyomi.domain.items.chapter.service.calculateChapterGap
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ChapterTransition(
    transition: ReaderChapterTransition,
    currChapterDownloaded: Boolean,
    goingToChapterDownloaded: Boolean,
    previousChapter: Chapter? = null,
    nextChapter: Chapter? = null,
    onOpenPrevious: (() -> Unit)? = null,
    onOpenNext: (() -> Unit)? = null,
) {
    val currentChapter = transition.from.chapter.toDomainChapter()
    val chapterGap = calculateChapterGap(nextChapter, currentChapter)

    Column(
        modifier = Modifier
            .widthIn(max = 460.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ReaderTerminalSummary(
            chapter = currentChapter,
            currChapterDownloaded = currChapterDownloaded,
            goingToChapterDownloaded = goingToChapterDownloaded,
        )

        if (chapterGap > 0) {
            ReaderTerminalGapWarning(
                gapCount = chapterGap,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReaderTerminalButton(
                modifier = Modifier.weight(1f),
                title = stringResource(MR.strings.action_previous_chapter),
                chapter = previousChapter,
                iconStart = Icons.AutoMirrored.Filled.ArrowBack,
                emphasized = false,
                enabled = previousChapter != null && onOpenPrevious != null,
                onClick = onOpenPrevious,
            )
            ReaderTerminalButton(
                modifier = Modifier.weight(1f),
                title = stringResource(MR.strings.action_next_chapter),
                chapter = nextChapter,
                iconEnd = Icons.AutoMirrored.Filled.ArrowForward,
                emphasized = true,
                enabled = nextChapter != null && onOpenNext != null,
                onClick = onOpenNext,
            )
        }

        EtsumeCommentPlaceholder(
            draftKey = "reader_manga_${currentChapter?.id ?: 0L}",
        )
    }
}

@Composable
private fun ReaderTerminalSummary(
    chapter: Chapter?,
    currChapterDownloaded: Boolean,
    goingToChapterDownloaded: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = etsumeGlassContainerColor(0.56f),
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(MR.strings.transition_finished),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = chapter?.name ?: stringResource(MR.strings.unknown),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = when {
                    currChapterDownloaded && goingToChapterDownloaded ->
                        "Saved locally. Use the buttons below to move between chapters."
                    currChapterDownloaded ->
                        "This chapter is saved locally. Next and previous stay manual."
                    else ->
                        "Chapter ended here. Use the buttons below to continue."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReaderTerminalGapWarning(
    gapCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.22f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.26f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
            Text(
                text = pluralStringResource(
                    MR.plurals.missing_chapters_warning,
                    count = gapCount,
                    gapCount,
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ReaderTerminalButton(
    title: String,
    chapter: Chapter?,
    emphasized: Boolean,
    enabled: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    iconStart: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconEnd: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    val backgroundColor = if (emphasized) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    } else {
        etsumeGlassContainerColor(0.56f)
    }
    val borderColor = if (emphasized) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
    } else {
        etsumeGlassBorderColor(0.24f)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = enabled) { onClick?.invoke() },
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box {
            if (emphasized && enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(etsumeAccentBrush(alpha = 0.92f)),
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    iconStart?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = if (enabled) {
                                if (emphasized) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            },
                        )
                    }
                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (enabled) {
                            if (emphasized) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        },
                        textAlign = if (iconStart != null && iconEnd == null) TextAlign.End else TextAlign.Start,
                    )
                    iconEnd?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = if (enabled) {
                                if (emphasized) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            },
                        )
                    }
                }
                Text(
                    text = chapter?.let(::chapterToken) ?: "Unavailable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) {
                        if (emphasized) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    },
                )
                Text(
                    text = chapter?.name ?: "No chapter available",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) {
                        if (emphasized) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.86f) else MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.40f)
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun chapterToken(chapter: Chapter): String {
    return if (chapter.isRecognizedNumber) {
        val number = chapter.chapterNumber
        val formatted = if (number == number.toInt().toDouble()) {
            number.toInt().toString()
        } else {
            number.toString()
        }
        "Ch. $formatted"
    } else {
        chapter.name
    }
}
