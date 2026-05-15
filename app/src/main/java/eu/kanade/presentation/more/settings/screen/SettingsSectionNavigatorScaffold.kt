package eu.kanade.presentation.more.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import dev.icerock.moko.resources.StringResource
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarTitle
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.presentation.more.settings.widget.TextPreferenceWidget
import eu.kanade.presentation.theme.EtsumeAuroraBackdrop
import eu.kanade.tachiyomi.R
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import cafe.adriel.voyager.core.screen.Screen as VoyagerScreen

data class SettingsSectionItem(
    val titleRes: StringResource,
    val subtitleRes: StringResource? = null,
    val formatSubtitle: @Composable () -> String? = { subtitleRes?.let { stringResource(it) } },
    val icon: ImageVector,
    val screen: VoyagerScreen,
)

@Composable
fun SettingsSectionNavigatorScaffold(
    title: String,
    subtitle: String?,
    items: List<SettingsSectionItem>,
    twoPane: Boolean,
    showBrandIcon: Boolean = false,
    onBackPressed: () -> Unit,
    onSearchClick: (() -> Unit)? = null,
    isSelected: (SettingsSectionItem) -> Boolean,
    onNavigate: (VoyagerScreen) -> Unit,
) {
    val containerColor = if (twoPane) rememberPalerSurface() else Color.Transparent
    val topBarState = rememberTopAppBarState()

    Scaffold(
        topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState),
        topBar = { scrollBehavior ->
            AppBar(
                titleContent = {
                    if (showBrandIcon) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Image(
                                painter = painterResource(R.drawable.app_launcher),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit,
                            )
                            AppBarTitle(title = title, subtitle = subtitle)
                        }
                    } else {
                        AppBarTitle(title = title, subtitle = subtitle)
                    }
                },
                navigateUp = onBackPressed,
                actions = {
                    if (onSearchClick != null) {
                        AppBarActions(
                            persistentListOf(
                                AppBar.Action(
                                    title = stringResource(tachiyomi.i18n.MR.strings.action_search),
                                    icon = Icons.Outlined.Search,
                                    onClick = onSearchClick,
                                ),
                            ),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = containerColor,
    ) { contentPadding ->
        val state = rememberLazyListState()
        val selectedIndex = if (twoPane) {
            items.indexOfFirst(isSelected)
                .also {
                    LaunchedEffect(it) {
                        if (it > 0) {
                            state.animateScrollToItem(it + if (subtitle != null) 1 else 0)
                            topBarState.contentOffset = topBarState.heightOffsetLimit
                        }
                    }
                }
        } else {
            null
        }

        Box(modifier = Modifier.fillMaxSize()) {
            EtsumeAuroraBackdrop()
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(horizontal = 12.dp),
                state = state,
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(
                    items = items,
                    key = { _, item -> item.hashCode() },
                ) { index, item ->
                    val selected = selectedIndex == index
                    var modifier: Modifier = Modifier
                    var contentColor = LocalContentColor.current
                    if (twoPane) {
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .then(
                                if (selected) {
                                    Modifier.background(etsumeAccentBrush(0.14f))
                                } else {
                                    Modifier
                                },
                            )
                        if (selected) {
                            contentColor = MaterialTheme.colorScheme.onSurface
                        }
                    }
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        TextPreferenceWidget(
                            modifier = modifier,
                            title = stringResource(item.titleRes),
                            subtitle = item.formatSubtitle(),
                            icon = item.icon,
                            onPreferenceClick = { onNavigate(item.screen) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsIntroCard(
    title: String,
    subtitle: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun rememberPalerSurface(): Color {
    val surface = MaterialTheme.colorScheme.surface
    val dark = isSystemInDarkTheme()
    return remember(surface, dark) {
        val arr = FloatArray(3)
        ColorUtils.colorToHSL(surface.toArgb(), arr)
        arr[2] = if (dark) {
            arr[2] - 0.05f
        } else {
            arr[2] + 0.02f
        }.coerceIn(0f, 1f)
        Color.hsl(arr[0], arr[1], arr[2])
    }
}


