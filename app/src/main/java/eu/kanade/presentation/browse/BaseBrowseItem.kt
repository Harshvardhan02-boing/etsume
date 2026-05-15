package eu.kanade.presentation.browse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import tachiyomi.presentation.core.components.material.padding

@Composable
fun BaseBrowseItem(
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit = {},
    onLongClickItem: () -> Unit = {},
    icon: @Composable RowScope.() -> Unit = {},
    action: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth(),
        color = etsumeGlassContainerColor(0.5f),
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.28f)),
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(
                    onClick = onClickItem,
                    onLongClick = onLongClickItem,
                )
                .padding(
                    horizontal = MaterialTheme.padding.medium,
                    vertical = 13.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            content()
            action()
        }
    }
}

@Composable
fun BrowseActionChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(etsumeAccentBrush(0.94f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun BrowseIconActionButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        color = if (emphasized) Color.Transparent else etsumeGlassContainerColor(0.5f),
        contentColor = if (emphasized) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = if (emphasized) {
            null
        } else {
            BorderStroke(1.dp, etsumeGlassBorderColor(0.22f))
        },
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .then(
                    if (emphasized) Modifier.background(etsumeAccentBrush(0.94f)) else Modifier,
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}
