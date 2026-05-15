package eu.kanade.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.icerock.moko.resources.StringResource
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.TabText
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun TabbedScreen(
    titleRes: StringResource?,
    tabs: ImmutableList<TabContent>,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState { tabs.size },
    mangaSearchQuery: String? = null,
    onChangeMangaSearchQuery: (String?) -> Unit = {},
    scrollable: Boolean = false,
    animeSearchQuery: String? = null,
    onChangeAnimeSearchQuery: (String?) -> Unit = {},

) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            if (titleRes != null) {
                val tab = tabs[state.currentPage]
                val searchEnabled = tab.searchEnabled

                val actualQuery = when (state.currentPage % 2) {
                    1 -> mangaSearchQuery // History and Browse
                    else -> animeSearchQuery
                }

                val actualOnChange = when (state.currentPage % 2) {
                    1 -> onChangeMangaSearchQuery // History and Browse
                    else -> onChangeAnimeSearchQuery
                }

                SearchToolbar(
                    titleContent = {
                        AppBarTitle(
                            stringResource(titleRes),
                            modifier = modifier,
                            null,
                            tab.numberTitle,
                        )
                    },
                    searchEnabled = searchEnabled,
                    searchQuery = if (searchEnabled) actualQuery else null,
                    onChangeSearchQuery = actualOnChange,
                    actions = { AppBarActions(tab.actions) },
                    navigateUp = tab.navigateUp,
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .background(Color.Transparent),
        ) {
            if (scrollable) {
                ScrollableTabRow(
                    selectedTabIndex = state.currentPage,
                    edgePadding = 13.dp,
                    modifier = Modifier.zIndex(1f),
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = state.currentPage == index,
                            onClick = { scope.launch { state.animateScrollToPage(index) } },
                            text = {
                                TabText(
                                    text = stringResource(tab.titleRes),
                                    badgeCount = tab.badgeNumber,
                                )
                            },
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            } else {
                SegmentedTabRow(
                    tabs = tabs,
                    selectedIndex = state.currentPage,
                    onSelect = { index ->
                        scope.launch { state.animateScrollToPage(index) }
                    },
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp,
                        ),
                )
            }

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = state,
                verticalAlignment = Alignment.Top,
            ) { page ->
                tabs[page].content(
                    PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                    snackbarHostState,
                )
            }
        }
    }
}

data class TabContent(
    val titleRes: StringResource,
    val badgeNumber: Int? = null,
    val searchEnabled: Boolean = false,
    val actions: ImmutableList<AppBar.AppBarAction> = persistentListOf(),
    val content: @Composable (contentPadding: PaddingValues, snackbarHostState: SnackbarHostState) -> Unit,
    val numberTitle: Int = 0,
    val cancelAction: () -> Unit = {},
    val navigateUp: (() -> Unit)? = null,
)

@Composable
private fun SegmentedTabRow(
    tabs: ImmutableList<TabContent>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = etsumeGlassContainerColor(0.52f),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(
            width = 1.dp,
            color = etsumeGlassBorderColor(0.22f),
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = selectedIndex == index
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(index) },
                    color = Color.Transparent,
                    contentColor = if (selected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.extraLarge)
                            .then(if (selected) Modifier.background(etsumeAccentBrush(0.96f)) else Modifier)
                            .padding(
                                horizontal = 12.dp,
                                vertical = 12.dp,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        TabText(
                            text = stringResource(tab.titleRes),
                            badgeCount = tab.badgeNumber,
                        )
                    }
                }
            }
        }
    }
}
