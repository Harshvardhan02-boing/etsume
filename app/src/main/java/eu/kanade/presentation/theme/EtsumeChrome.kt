package eu.kanade.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun EtsumeAuroraBackdrop(
    modifier: Modifier = Modifier,
) {
    val background = MaterialTheme.colorScheme.background
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .drawWithCache {
                val shellGradient = Brush.verticalGradient(
                    colors = listOf(
                        background.copy(alpha = 0.98f),
                        background,
                        background.copy(alpha = 0.99f),
                    ),
                )
                val topSweep = Brush.linearGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.11f),
                        secondary.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                    start = Offset(size.width * 0.08f, 0f),
                    end = Offset(size.width * 0.92f, size.height * 0.38f),
                )
                val cornerBloom = Brush.radialGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.13f),
                        secondary.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.18f, size.height * 0.10f),
                    radius = size.minDimension * 0.56f,
                )
                onDrawBehind {
                    drawRect(shellGradient)
                    drawRect(topSweep)
                    drawRect(cornerBloom)
                }
            },
    )
}

@Composable
@ReadOnlyComposable
fun etsumeGlassContainerColor(alpha: Float = 0.56f): Color {
    return MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = alpha)
}

@Composable
@ReadOnlyComposable
fun etsumeGlassBorderColor(alpha: Float = 0.24f): Color {
    return MaterialTheme.colorScheme.outline.copy(alpha = alpha)
}

@Composable
@ReadOnlyComposable
fun etsumeAccentBrush(alpha: Float = 1f): Brush {
    return Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = alpha),
            MaterialTheme.colorScheme.secondary.copy(alpha = alpha),
        ),
    )
}
