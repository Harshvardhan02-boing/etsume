package eu.kanade.tachiyomi.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.DropdownMenu
import eu.kanade.presentation.components.EtsumeToolbarActionButton
import eu.kanade.presentation.entries.components.ItemCover
import eu.kanade.presentation.components.RadioMenuItem
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.presentation.util.isBlockedExplicitContent
import eu.kanade.presentation.util.formatChapterNumber
import eu.kanade.presentation.util.formatEpisodeNumber
import eu.kanade.presentation.util.Tab
import eu.kanade.tachiyomi.R
import eu.kanade.domain.base.BasePreferences
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.tachiyomi.extension.anime.AnimeExtensionManager
import eu.kanade.tachiyomi.extension.manga.MangaExtensionManager
import eu.kanade.tachiyomi.ui.download.DownloadsTab
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.ui.history.HistoriesTab
import eu.kanade.tachiyomi.ui.player.settings.PlayerPreferences
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import mihon.feature.upcoming.anime.UpcomingAnimeScreen
import mihon.feature.upcoming.manga.UpcomingMangaScreen
import tachiyomi.domain.history.anime.model.AnimeHistoryWithRelations
import tachiyomi.domain.history.anime.repository.AnimeHistoryRepository
import tachiyomi.domain.history.manga.model.MangaHistoryWithRelations
import tachiyomi.domain.history.manga.repository.MangaHistoryRepository
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

data object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = androidStringResource(R.string.etsume_label_home),
            icon = rememberVectorPainter(Icons.Outlined.Home),
        )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val scope = androidx.compose.runtime.rememberCoroutineScope()
        var selectedMedia by rememberSaveable { mutableStateOf(HomeMedia.Manga) }
        val mangaHistory by Injekt.get<MangaHistoryRepository>().getMangaHistory("").collectAsState(initial = emptyList())
        val animeHistory by Injekt.get<AnimeHistoryRepository>().getAnimeHistory("").collectAsState(initial = emptyList())
        val basePreferences = Injekt.get<BasePreferences>()
        val sourcePreferences = Injekt.get<SourcePreferences>()
        val mangaExtensionManager = Injekt.get<MangaExtensionManager>()
        val animeExtensionManager = Injekt.get<AnimeExtensionManager>()
        val playerPreferences = Injekt.get<PlayerPreferences>()
        val incognitoMode by basePreferences.incognitoMode().changes().collectAsState(initial = basePreferences.incognitoMode().get())
        val showExplicitTitles by basePreferences.showExplicitTitles().changes().collectAsState(initial = basePreferences.showExplicitTitles().get())
        var showQuickMenu by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (!basePreferences.explicitTitlesPrefMigrated().get()) {
                sourcePreferences.showNsfwSource().set(true)
                basePreferences.explicitTitlesPrefMigrated().set(true)
            }
        }

        tachiyomi.presentation.core.components.material.Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AppBar(
                    titleContent = {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = androidStringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = androidStringResource(R.string.etsume_home_brand_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    actions = {
                        EtsumeToolbarActionButton(
                            onClick = { showQuickMenu = !showQuickMenu },
                            contentDescription = androidStringResource(R.string.etsume_label_home),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = null,
                            )
                        }
                        DropdownMenu(
                            expanded = showQuickMenu,
                            onDismissRequest = { showQuickMenu = false },
                        ) {
                            RadioMenuItem(
                                text = { Text(androidStringResource(R.string.etsume_action_show_nsfw)) },
                                isChecked = showExplicitTitles,
                                onClick = {
                                    val nextValue = !showExplicitTitles
                                    basePreferences.showExplicitTitles().set(nextValue)
                                    showQuickMenu = false
                                },
                            )
                            RadioMenuItem(
                                text = { Text(androidStringResource(R.string.etsume_action_incognito)) },
                                isChecked = incognitoMode,
                                onClick = {
                                    val nextValue = !incognitoMode
                                    basePreferences.incognitoMode().set(nextValue)
                                    showQuickMenu = false
                                },
                            )
                        }
                    },
                )
            },
        ) { contentPadding ->
            ScrollbarLazyColumn(
                modifier = Modifier.padding(contentPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    HomeSegmentedRow(
                        selectedMedia = selectedMedia,
                        onSelect = { selectedMedia = it },
                    )
                }
                item {
                    HomeHeroCard(
                        title = androidStringResource(
                            if (selectedMedia == HomeMedia.Manga) {
                                R.string.etsume_home_manga_title
                            } else {
                                R.string.etsume_home_anime_title
                            },
                        ),
                        subtitle = androidStringResource(
                            if (selectedMedia == HomeMedia.Manga) {
                                R.string.etsume_home_manga_subtitle
                            } else {
                                R.string.etsume_home_anime_subtitle
                            },
                        ),
                        primaryLabel = androidStringResource(R.string.etsume_home_action_browse),
                        secondaryLabel = androidStringResource(R.string.etsume_home_action_library),
                        onPrimary = {
                            scope.launch {
                                HomeScreen.openTab(
                                    HomeScreen.Tab.Browse(
                                        toExtensions = false,
                                        anime = selectedMedia == HomeMedia.Anime,
                                    ),
                                )
                            }
                        },
                        onSecondary = {
                            scope.launch {
                                HomeScreen.openTab(
                                    if (selectedMedia == HomeMedia.Manga) {
                                        HomeScreen.Tab.Library()
                                    } else {
                                        HomeScreen.Tab.AnimeLib()
                                    },
                                )
                            }
                        },
                    )
                }
                item {
                    SectionHeader(
                        title = androidStringResource(R.string.etsume_home_section_continue),
                        actionLabel = androidStringResource(R.string.etsume_action_show_all),
                        onAction = { navigator.push(HistoriesTab) },
                    )
                }
                item {
                    HomeContinueGrid(
                        selectedMedia = selectedMedia,
                        showNsfw = showExplicitTitles,
                        mangaHistory = mangaHistory,
                        animeHistory = animeHistory,
                        mangaSourceIsNsfw = mangaExtensionManager::isSourceNsfw,
                        animeSourceIsNsfw = animeExtensionManager::isSourceNsfw,
                        onResumeManga = { mangaId, chapterId ->
                            context.startActivity(ReaderActivity.newIntent(context, mangaId, chapterId))
                        },
                        onResumeAnime = { animeId, episodeId ->
                            scope.launch {
                                MainActivity.startPlayerActivity(
                                    context = context,
                                    animeId = animeId,
                                    episodeId = episodeId,
                                    extPlayer = playerPreferences.alwaysUseExternalPlayer().get(),
                                )
                            }
                        },
                    )
                }
                item {
                    SectionHeader(androidStringResource(R.string.etsume_home_section_shortcuts))
                }
                item {
                    HomeActionGrid(
                        items = persistentListOf(
                            HomeActionItem(
                                title = androidStringResource(R.string.etsume_home_action_upcoming),
                                subtitle = androidStringResource(R.string.etsume_home_action_upcoming_subtitle),
                                icon = Icons.Outlined.TravelExplore,
                                onClick = {
                                    navigator.push(
                                        if (selectedMedia == HomeMedia.Manga) {
                                            UpcomingMangaScreen()
                                        } else {
                                            UpcomingAnimeScreen()
                                        },
                                    )
                                },
                            ),
                            HomeActionItem(
                                title = androidStringResource(R.string.etsume_home_action_history),
                                subtitle = androidStringResource(R.string.etsume_home_action_history_subtitle),
                                icon = Icons.Outlined.History,
                                onClick = { navigator.push(HistoriesTab) },
                            ),
                            HomeActionItem(
                                title = androidStringResource(R.string.etsume_home_action_downloads),
                                subtitle = androidStringResource(R.string.etsume_home_action_downloads_subtitle),
                                icon = Icons.Outlined.Download,
                                onClick = { navigator.push(DownloadsTab) },
                            ),
                            HomeActionItem(
                                title = androidStringResource(R.string.etsume_home_action_sources),
                                subtitle = androidStringResource(R.string.etsume_home_action_sources_subtitle),
                                icon = Icons.Outlined.TravelExplore,
                                onClick = {
                                    scope.launch {
                                        HomeScreen.openTab(
                                            HomeScreen.Tab.Browse(
                                                toExtensions = false,
                                                anime = selectedMedia == HomeMedia.Anime,
                                            ),
                                        )
                                    }
                                },
                            ),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSegmentedRow(
    selectedMedia: HomeMedia,
    onSelect: (HomeMedia) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = etsumeGlassContainerColor(0.5f),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            HomeMedia.entries.forEach { media ->
                val selected = media == selectedMedia
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(media) },
                    color = Color.Transparent,
                    contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.extraLarge)
                            .then(if (selected) Modifier.background(etsumeAccentBrush(0.96f)) else Modifier)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = androidStringResource(
                                if (media == HomeMedia.Manga) R.string.etsume_label_manga else R.string.etsume_label_anime,
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeroCard(
    title: String,
    subtitle: String,
    primaryLabel: String,
    secondaryLabel: String,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = etsumeGlassContainerColor(0.52f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HomeChipButton(
                    label = primaryLabel,
                    modifier = Modifier.weight(1f),
                    emphasized = true,
                    onClick = onPrimary,
                )
                HomeChipButton(
                    label = secondaryLabel,
                    modifier = Modifier.weight(1f),
                    emphasized = false,
                    onClick = onSecondary,
                )
            }
        }
    }
}

private data class HomeActionItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
)

private data class HomeContinueItem(
    val title: String,
    val subtitle: String,
    val coverData: Any?,
    val onClick: () -> Unit,
)

private enum class HomeMedia {
    Manga,
    Anime,
}

@Composable
private fun HomeActionGrid(
    items: kotlinx.collections.immutable.ImmutableList<HomeActionItem>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowItems.forEach { item ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 156.dp)
                            .clickable(onClick = item.onClick),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = etsumeGlassContainerColor(0.52f),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = MaterialTheme.shapes.large,
                                color = etsumeGlassContainerColor(0.46f),
                                tonalElevation = 0.dp,
                                shadowElevation = 0.dp,
                                border = BorderStroke(1.dp, etsumeGlassBorderColor(0.18f)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.large)
                                        .background(etsumeAccentBrush(0.18f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeContinueGrid(
    selectedMedia: HomeMedia,
    showNsfw: Boolean,
    mangaHistory: List<MangaHistoryWithRelations>,
    animeHistory: List<AnimeHistoryWithRelations>,
    mangaSourceIsNsfw: (Long) -> Boolean,
    animeSourceIsNsfw: (Long) -> Boolean,
    onResumeManga: (Long, Long) -> Unit,
    onResumeAnime: (Long, Long) -> Unit,
) {
    val items = if (selectedMedia == HomeMedia.Manga) {
        mangaHistory
            .filter {
                showNsfw || (
                    !isBlockedExplicitContent(
                        showNsfw = showNsfw,
                        fields = listOf(it.title),
                        sourceId = it.coverData.sourceId,
                    ) && !mangaSourceIsNsfw(it.coverData.sourceId)
                )
            }
            .take(2)
            .map {
                HomeContinueItem(
                    title = it.title,
                    subtitle = androidStringResource(
                        R.string.etsume_continue_chapter,
                        formatChapterNumber(it.chapterNumber),
                    ),
                    coverData = it.coverData,
                    onClick = { onResumeManga(it.mangaId, it.chapterId) },
                )
            }
    } else {
        animeHistory
            .filter {
                showNsfw || (
                    !isBlockedExplicitContent(
                        showNsfw = showNsfw,
                        fields = listOf(it.title),
                        sourceId = it.coverData.sourceId,
                    ) && !animeSourceIsNsfw(it.coverData.sourceId)
                )
            }
            .take(2)
            .map {
                HomeContinueItem(
                    title = it.title,
                    subtitle = androidStringResource(
                        R.string.etsume_continue_episode,
                        formatEpisodeNumber(it.episodeNumber),
                    ),
                    coverData = it.coverData,
                    onClick = { onResumeAnime(it.animeId, it.episodeId) },
                )
            }
    }

    if (items.isEmpty()) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = etsumeGlassContainerColor(0.52f),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = androidStringResource(R.string.etsume_continue_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = androidStringResource(
                        if (selectedMedia == HomeMedia.Manga) {
                            R.string.etsume_continue_empty_manga
                        } else {
                            R.string.etsume_continue_empty_anime
                        },
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { item ->
            HomeContinueCard(item = item)
        }
    }
}

@Composable
private fun HomeContinueCard(
    item: HomeContinueItem,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = etsumeGlassContainerColor(0.54f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = item.onClick)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ItemCover.Book(
                data = item.coverData,
                modifier = Modifier
                    .size(width = 72.dp, height = 108.dp)
                    .fillMaxHeight(),
                shape = MaterialTheme.shapes.large,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HomeChipButton(
                        label = androidStringResource(R.string.etsume_action_continue),
                        emphasized = true,
                        onClick = item.onClick,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeChipButton(
    label: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (emphasized) Color.Transparent else etsumeGlassContainerColor(0.48f),
        contentColor = if (emphasized) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = if (emphasized) null else BorderStroke(1.dp, etsumeGlassBorderColor(0.22f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .then(if (emphasized) Modifier.background(etsumeAccentBrush(0.96f)) else Modifier)
                .padding(vertical = 12.dp),
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
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (actionLabel != null && onAction != null) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = etsumeGlassContainerColor(0.5f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                border = BorderStroke(1.dp, etsumeGlassBorderColor(0.22f)),
                onClick = onAction,
            ) {
                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(etsumeAccentBrush(0.12f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
