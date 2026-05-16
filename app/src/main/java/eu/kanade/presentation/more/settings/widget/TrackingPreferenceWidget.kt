package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.more.settings.LocalPreferenceHighlighted
import eu.kanade.presentation.track.components.EtsumeTrackCard
import eu.kanade.presentation.track.components.EtsumeTrackPill
import eu.kanade.presentation.track.components.TrackLogoIcon
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.track.EnhancedAnimeTracker
import eu.kanade.tachiyomi.data.track.EnhancedMangaTracker
import eu.kanade.tachiyomi.data.track.Tracker

@Composable
fun TrackingPreferenceWidget(
    modifier: Modifier = Modifier,
    tracker: Tracker,
    checked: Boolean,
    onClick: (() -> Unit)? = null,
) {
    val highlighted = LocalPreferenceHighlighted.current
    val username = tracker.getUsername().takeIf { it.isNotBlank() }
    val isEnhanced = tracker is EnhancedMangaTracker || tracker is EnhancedAnimeTracker
    val subtitle = when {
        checked && !username.isNullOrBlank() -> username
        checked && isEnhanced -> androidStringResource(R.string.etsume_tracking_enhanced_active)
        checked -> androidStringResource(R.string.etsume_tracking_connected)
        else -> androidStringResource(R.string.etsume_tracking_tap_to_connect)
    }

    Box(modifier = Modifier.highlightBackground(highlighted)) {
        EtsumeTrackCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = PrefsHorizontalPadding, vertical = 4.dp),
            selected = checked,
        ) {
            Row(
                modifier = Modifier
                    .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrackLogoIcon(tracker)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                ) {
                    Text(
                        text = tracker.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                EtsumeTrackPill(
                    text = if (checked) {
                        androidStringResource(R.string.etsume_tracking_manage)
                    } else {
                        androidStringResource(R.string.etsume_tracking_connect)
                    },
                    emphasized = checked,
                )
            }
        }
    }
}
