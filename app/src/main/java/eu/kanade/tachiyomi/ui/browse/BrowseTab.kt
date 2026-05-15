package eu.kanade.tachiyomi.ui.browse

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import eu.kanade.presentation.category.components.CategoryFloatingActionButton
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.components.AppBarTitle
import eu.kanade.presentation.components.SearchToolbar
import eu.kanade.presentation.components.TabContent
import eu.kanade.presentation.more.settings.screen.browse.AnimeExtensionReposScreenModel
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.presentation.more.settings.screen.browse.MangaExtensionReposScreenModel
import eu.kanade.presentation.more.settings.screen.browse.RepoDialog
import eu.kanade.presentation.more.settings.screen.browse.RepoEvent
import eu.kanade.presentation.more.settings.screen.browse.RepoScreenState
import eu.kanade.presentation.more.settings.screen.browse.components.ExtensionRepoConfirmDialog
import eu.kanade.presentation.more.settings.screen.browse.components.ExtensionRepoConflictDialog
import eu.kanade.presentation.more.settings.screen.browse.components.ExtensionRepoCreateDialog
import eu.kanade.presentation.more.settings.screen.browse.components.ExtensionRepoDeleteDialog
import eu.kanade.presentation.more.settings.screen.browse.components.ExtensionReposContent
import eu.kanade.presentation.util.Tab
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.browse.anime.extension.AnimeExtensionsScreenModel
import eu.kanade.tachiyomi.ui.browse.anime.extension.animeExtensionsTab
import eu.kanade.tachiyomi.ui.browse.anime.migration.sources.migrateAnimeSourceTab
import eu.kanade.tachiyomi.ui.browse.anime.source.animeSourcesTab
import eu.kanade.tachiyomi.ui.browse.anime.source.globalsearch.GlobalAnimeSearchScreen
import eu.kanade.tachiyomi.ui.browse.manga.extension.MangaExtensionsScreenModel
import eu.kanade.tachiyomi.ui.browse.manga.extension.mangaExtensionsTab
import eu.kanade.tachiyomi.ui.browse.manga.migration.sources.migrateMangaSourceTab
import eu.kanade.tachiyomi.ui.browse.manga.source.globalsearch.GlobalMangaSearchScreen
import eu.kanade.tachiyomi.ui.browse.manga.source.mangaSourcesTab
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.util.system.openInBrowser
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import mihon.domain.extensionrepo.model.ExtensionRepo
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.components.material.topSmallPaddingValues
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen
import tachiyomi.presentation.core.util.plus

data object BrowseTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current is BrowseTab
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_browse_enter)
            return TabOptions(
                index = 1u,
                title = stringResource(MR.strings.browse),
                icon = rememberAnimatedVectorPainter(image, isSelected),
            )
        }

    private var latestTarget = BrowseTarget(BrowseMedia.Manga, BrowsePane.Sources)
    private val switchToTargetChannel = Channel<BrowseTarget>(1, BufferOverflow.DROP_OLDEST)

    override suspend fun onReselect(navigator: Navigator) {
        navigator.push(
            when (latestTarget.media) {
                BrowseMedia.Manga -> GlobalMangaSearchScreen()
                BrowseMedia.Anime -> GlobalAnimeSearchScreen()
            },
        )
    }

    fun showExtension() {
        switchToTargetChannel.trySend(BrowseTarget(BrowseMedia.Manga, BrowsePane.Extensions))
    }

    fun showAnimeExtension() {
        switchToTargetChannel.trySend(BrowseTarget(BrowseMedia.Anime, BrowsePane.Extensions))
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val mangaExtensionsScreenModel = rememberScreenModel { MangaExtensionsScreenModel() }
        val mangaExtensionsState by mangaExtensionsScreenModel.state.collectAsState()
        val animeExtensionsScreenModel = rememberScreenModel { AnimeExtensionsScreenModel() }
        val animeExtensionsState by animeExtensionsScreenModel.state.collectAsState()

        val mangaReposScreenModel = rememberScreenModel { MangaExtensionReposScreenModel() }
        val mangaReposState by mangaReposScreenModel.state.collectAsState()
        val animeReposScreenModel = rememberScreenModel { AnimeExtensionReposScreenModel() }
        val animeReposState by animeReposScreenModel.state.collectAsState()

        var selectedMedia by rememberSaveable { mutableStateOf(BrowseMedia.Manga) }
        var selectedPane by rememberSaveable { mutableStateOf(BrowsePane.Sources) }
        val snackbarHostState = remember { SnackbarHostState() }

        val animeSourcesTab = animeSourcesTab()
        val mangaSourcesTab = mangaSourcesTab()
        val animeExtensionsTab = animeExtensionsTab(animeExtensionsScreenModel)
        val mangaExtensionsTab = mangaExtensionsTab(mangaExtensionsScreenModel)

        val animeRepoListState = rememberLazyListState()
        val mangaRepoListState = rememberLazyListState()

        val currentTab = when (selectedMedia) {
            BrowseMedia.Manga -> {
                when (selectedPane) {
                    BrowsePane.Sources -> mangaSourcesTab
                    BrowsePane.Extensions -> mangaExtensionsTab
                    BrowsePane.Repos -> browseReposTab(
                        onRefresh = mangaReposScreenModel::refreshRepos,
                        state = mangaReposState,
                        lazyListState = mangaRepoListState,
                        onOpenWebsite = { context.openInBrowser(it.website) },
                        onClickDelete = { mangaReposScreenModel.showDialog(RepoDialog.Delete(it)) },
                    )
                }
            }
            BrowseMedia.Anime -> {
                when (selectedPane) {
                    BrowsePane.Sources -> animeSourcesTab
                    BrowsePane.Extensions -> animeExtensionsTab
                    BrowsePane.Repos -> browseReposTab(
                        onRefresh = animeReposScreenModel::refreshRepos,
                        state = animeReposState,
                        lazyListState = animeRepoListState,
                        onOpenWebsite = { context.openInBrowser(it.website) },
                        onClickDelete = { animeReposScreenModel.showDialog(RepoDialog.Delete(it)) },
                    )
                }
            }
        }

        latestTarget = BrowseTarget(selectedMedia, selectedPane)

        val currentSearchQuery = when (selectedPane) {
            BrowsePane.Extensions -> when (selectedMedia) {
                BrowseMedia.Manga -> mangaExtensionsState.searchQuery
                BrowseMedia.Anime -> animeExtensionsState.searchQuery
            }
            else -> null
        }
        val onChangeSearchQuery: (String?) -> Unit = when (selectedPane) {
            BrowsePane.Extensions -> when (selectedMedia) {
                BrowseMedia.Manga -> mangaExtensionsScreenModel::search
                BrowseMedia.Anime -> animeExtensionsScreenModel::search
            }
            else -> { _ -> }
        }

        val migrateActionTitle = when (selectedMedia) {
            BrowseMedia.Manga -> androidStringResource(R.string.etsume_label_migrate_manga)
            BrowseMedia.Anime -> androidStringResource(R.string.etsume_label_migrate_anime)
        }
        val appBarActions = buildList {
            addAll(currentTab.actions)
            if (selectedPane == BrowsePane.Sources) {
                add(
                    AppBar.OverflowAction(
                        title = migrateActionTitle,
                        onClick = {
                            navigator.push(
                                when (selectedMedia) {
                                    BrowseMedia.Manga -> MigrateSourcesScreen(BrowseMedia.Manga)
                                    BrowseMedia.Anime -> MigrateSourcesScreen(BrowseMedia.Anime)
                                },
                            )
                        },
                    ),
                )
            }
        }.toPersistentList()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                SearchToolbar(
                    titleContent = {
                        AppBarTitle(
                            stringResource(MR.strings.browse),
                        )
                    },
                    searchEnabled = currentTab.searchEnabled,
                    searchQuery = currentSearchQuery,
                    onChangeSearchQuery = onChangeSearchQuery,
                    actions = { AppBarActions(appBarActions) },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                if (selectedPane == BrowsePane.Repos) {
                    CategoryFloatingActionButton(
                        lazyListState = when (selectedMedia) {
                            BrowseMedia.Manga -> mangaRepoListState
                            BrowseMedia.Anime -> animeRepoListState
                        },
                        onCreate = {
                            when (selectedMedia) {
                                BrowseMedia.Manga -> mangaReposScreenModel.showDialog(RepoDialog.Create)
                                BrowseMedia.Anime -> animeReposScreenModel.showDialog(RepoDialog.Create)
                            }
                        },
                    )
                }
            },
            contentWindowInsets = WindowInsets(0),
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = contentPadding.calculateTopPadding(),
                        start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                    ),
            ) {
                BrowseSegmentedControl(
                    labels = persistentListOf(
                        androidStringResource(R.string.etsume_label_manga),
                        androidStringResource(R.string.etsume_label_anime),
                    ),
                    selectedIndex = selectedMedia.ordinal,
                    onSelect = { selectedMedia = BrowseMedia.entries[it] },
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.padding.medium,
                        vertical = MaterialTheme.padding.small,
                    ),
                )

                BrowseSegmentedControl(
                    labels = persistentListOf(
                        androidStringResource(R.string.etsume_label_sources),
                        androidStringResource(R.string.etsume_label_extensions),
                        androidStringResource(R.string.etsume_label_repos),
                    ),
                    selectedIndex = selectedPane.ordinal,
                    onSelect = { selectedPane = BrowsePane.entries[it] },
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.padding.medium,
                    ),
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    currentTab.content(
                        PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                        snackbarHostState,
                    )
                }
            }

            RepoDialogs(
                state = when (selectedMedia) {
                    BrowseMedia.Manga -> mangaReposState
                    BrowseMedia.Anime -> animeReposState
                },
                onDismissRequest = {
                    when (selectedMedia) {
                        BrowseMedia.Manga -> mangaReposScreenModel.dismissDialog()
                        BrowseMedia.Anime -> animeReposScreenModel.dismissDialog()
                    }
                },
                onCreate = {
                    when (selectedMedia) {
                        BrowseMedia.Manga -> mangaReposScreenModel.createRepo(it)
                        BrowseMedia.Anime -> animeReposScreenModel.createRepo(it)
                    }
                },
                onDelete = {
                    when (selectedMedia) {
                        BrowseMedia.Manga -> mangaReposScreenModel.deleteRepo(it)
                        BrowseMedia.Anime -> animeReposScreenModel.deleteRepo(it)
                    }
                },
                onReplace = {
                    when (selectedMedia) {
                        BrowseMedia.Manga -> mangaReposScreenModel.replaceRepo(it)
                        BrowseMedia.Anime -> animeReposScreenModel.replaceRepo(it)
                    }
                },
            )
        }

        LaunchedEffect(Unit) {
            switchToTargetChannel.receiveAsFlow()
                .collectLatest {
                    selectedMedia = it.media
                    selectedPane = it.pane
                }
        }

        LaunchedEffect(Unit) {
            mangaReposScreenModel.events.collectLatest { event ->
                if (event is RepoEvent.LocalizedMessage) {
                    context.toast(event.stringRes)
                }
            }
        }

        LaunchedEffect(Unit) {
            animeReposScreenModel.events.collectLatest { event ->
                if (event is RepoEvent.LocalizedMessage) {
                    context.toast(event.stringRes)
                }
            }
        }

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }
    }
}

private enum class BrowseMedia {
    Manga,
    Anime,
}

private enum class BrowsePane {
    Sources,
    Extensions,
    Repos,
}

private data class BrowseTarget(
    val media: BrowseMedia,
    val pane: BrowsePane,
)

@Composable
private fun browseReposTab(
    state: RepoScreenState,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    onRefresh: () -> Unit,
    onOpenWebsite: (ExtensionRepo) -> Unit,
    onClickDelete: (String) -> Unit,
): TabContent {
    return TabContent(
        titleRes = MR.strings.label_extension_repos,
        actions = persistentListOf(
            AppBar.Action(
                title = stringResource(MR.strings.action_webview_refresh),
                icon = Icons.Outlined.Refresh,
                onClick = onRefresh,
            ),
        ),
        content = { contentPadding, _ ->
            when (state) {
                RepoScreenState.Loading -> LoadingScreen(Modifier.padding(contentPadding))
                is RepoScreenState.Success -> {
                    if (state.isEmpty) {
                        EmptyScreen(
                            stringRes = MR.strings.information_empty_repos,
                            modifier = Modifier.padding(contentPadding),
                        )
                    } else {
                        ExtensionReposContent(
                            repos = state.repos,
                            lazyListState = lazyListState,
                            paddingValues = contentPadding + topSmallPaddingValues +
                                PaddingValues(horizontal = MaterialTheme.padding.medium),
                            onOpenWebsite = onOpenWebsite,
                            onClickDelete = onClickDelete,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun RepoDialogs(
    state: RepoScreenState,
    onDismissRequest: () -> Unit,
    onCreate: (String) -> Unit,
    onDelete: (String) -> Unit,
    onReplace: (ExtensionRepo) -> Unit,
) {
    val successState = state as? RepoScreenState.Success ?: return

    when (val dialog = successState.dialog) {
        null -> Unit
        is RepoDialog.Create -> {
            ExtensionRepoCreateDialog(
                onDismissRequest = onDismissRequest,
                onCreate = onCreate,
                repoUrls = successState.repos.map { it.baseUrl }.toImmutableSet(),
            )
        }
        is RepoDialog.Delete -> {
            ExtensionRepoDeleteDialog(
                onDismissRequest = onDismissRequest,
                onDelete = { onDelete(dialog.repo) },
                repo = dialog.repo,
            )
        }
        is RepoDialog.Conflict -> {
            ExtensionRepoConflictDialog(
                oldRepo = dialog.oldRepo,
                newRepo = dialog.newRepo,
                onDismissRequest = onDismissRequest,
                onMigrate = { onReplace(dialog.newRepo) },
            )
        }
        is RepoDialog.Confirm -> {
            ExtensionRepoConfirmDialog(
                onDismissRequest = onDismissRequest,
                onCreate = { onCreate(dialog.url) },
                repo = dialog.url,
            )
        }
    }
}

@Composable
private fun BrowseSegmentedControl(
    labels: kotlinx.collections.immutable.ImmutableList<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = etsumeGlassContainerColor(0.54f),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(
            width = 1.dp,
            color = etsumeGlassBorderColor(0.24f),
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
            labels.forEachIndexed { index, label ->
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
                            .then(if (selected) Modifier.background(etsumeAccentBrush(0.95f)) else Modifier)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

private data class MigrateSourcesScreen(
    private val media: BrowseMedia,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val tab = when (media) {
            BrowseMedia.Manga -> mangaMigrationTab()
            BrowseMedia.Anime -> animeMigrationTab()
        }

        Scaffold(
            topBar = {
                SearchToolbar(
                    titleContent = { AppBarTitle(stringResource(tab.titleRes)) },
                    searchQuery = null,
                    onChangeSearchQuery = {},
                    searchEnabled = false,
                    navigateUp = navigator::pop,
                    actions = { AppBarActions(tab.actions) },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0),
        ) { contentPadding ->
            tab.content(contentPadding, snackbarHostState)
        }
    }

    @Composable
    private fun mangaMigrationTab(): TabContent = with(BrowseTab) { migrateMangaSourceTab() }

    @Composable
    private fun animeMigrationTab(): TabContent = with(BrowseTab) { migrateAnimeSourceTab() }
}


