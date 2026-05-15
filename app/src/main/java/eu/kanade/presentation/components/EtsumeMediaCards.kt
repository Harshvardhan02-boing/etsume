package eu.kanade.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.browse.BrowseActionChip
import eu.kanade.presentation.entries.components.ItemCover
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import tachiyomi.domain.entries.EntryCover as EntryCoverModel

@Composable
fun EtsumeMediaListCard(
    title: String,
    subtitle: String,
    coverData: EntryCoverModel,
    actionLabel: String,
    onClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.94f)
        } else {
            etsumeGlassContainerColor(0.5f)
        },
        border = BorderStroke(
            1.dp,
            if (isSelected) {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.32f)
            } else {
                etsumeGlassBorderColor(0.28f)
            },
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ItemCover.Book(
                data = coverData,
                modifier = Modifier.size(width = 72.dp, height = 104.dp),
                shape = RoundedCornerShape(20.dp),
            )
            Column(
                modifier = Modifier
                    .padding(start = 14.dp, end = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            BrowseActionChip(
                label = actionLabel,
                onClick = onActionClick,
            )
        }
    }
}

@Composable
fun EtsumeMediaGridCard(
    title: String,
    subtitle: String,
    coverData: EntryCoverModel,
    actionLabel: String,
    onClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.94f)
        } else {
            etsumeGlassContainerColor(0.52f)
        },
        border = BorderStroke(
            1.dp,
            if (isSelected) {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.32f)
            } else {
                etsumeGlassBorderColor(0.22f)
            },
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ItemCover.Book(
                data = coverData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = MaterialTheme.shapes.large,
            )
            Column(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(contentAlignment = Alignment.Center) {
                BrowseActionChip(
                    label = actionLabel,
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
