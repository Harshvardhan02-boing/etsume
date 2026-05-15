package eu.kanade.presentation.track.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.tachiyomi.data.track.Tracker
import tachiyomi.presentation.core.util.clickableNoIndication

@Composable
fun TrackLogoIcon(
    tracker: Tracker,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    val modifier = if (onClick != null) {
        Modifier.clickableNoIndication(onClick = onClick, onLongClick = onLongClick)
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .size(52.dp),
        shape = CircleShape,
        color = etsumeGlassContainerColor(0.72f),
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.28f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(tracker.getLogo()),
                contentDescription = tracker.name,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TrackLogoIconPreviews(
    @PreviewParameter(TrackLogoIconPreviewProvider::class)
    tracker: Tracker,
) {
    TachiyomiPreviewTheme {
        TrackLogoIcon(
            tracker = tracker,
            onClick = null,
            onLongClick = null,
        )
    }
}
