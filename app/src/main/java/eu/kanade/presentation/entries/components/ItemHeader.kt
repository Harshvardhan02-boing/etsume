package eu.kanade.presentation.entries.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.animesource.model.FetchType
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import tachiyomi.i18n.MR
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ItemHeader(
    enabled: Boolean,
    itemCount: Int?,
    missingItemsCount: Int,
    onClick: () -> Unit,
    isManga: Boolean,
    modifier: Modifier = Modifier,
    fetchType: FetchType = FetchType.Episodes,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = etsumeGlassContainerColor(0.56f),
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                )
                .padding(horizontal = MaterialTheme.padding.medium, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            ) {
                Text(
                    text = if (itemCount == null) {
                        val count = if (isManga) MR.strings.chapters else AYMR.strings.episodes
                        stringResource(count)
                    } else {
                        val pluralCount = if (isManga) {
                            MR.plurals.manga_num_chapters
                        } else {
                            when (fetchType) {
                                FetchType.Seasons -> AYMR.plurals.anime_num_seasons
                                FetchType.Episodes -> AYMR.plurals.anime_num_episodes
                            }
                        }
                        pluralStringResource(pluralCount, count = itemCount, itemCount)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
                if (itemCount != null) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Text(
                            text = itemCount.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .background(etsumeAccentBrush(0.94f), MaterialTheme.shapes.large)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            MissingItemsWarning(missingItemsCount)
        }
    }
}

@Composable
private fun MissingItemsWarning(count: Int) {
    if (count == 0) {
        return
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colorScheme.error,
    ) {
        Text(
            text = pluralStringResource(AYMR.plurals.missing_items, count = count, count),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
        )
    }
}
