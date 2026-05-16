package eu.kanade.tachiyomi.ui.library

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.presentation.util.Tab
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.library.anime.AnimeLibraryTab
import eu.kanade.tachiyomi.ui.library.manga.MangaLibraryTab
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

data object LibraryTab : Tab {

    enum class LibraryPane {
        Manga,
        Anime,
    }

    private var latestPane = LibraryPane.Manga
    private val switchPaneChannel = Channel<LibraryPane>(1, BufferOverflow.DROP_OLDEST)

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current.key == key
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_library_enter)
            return TabOptions(
                index = 2u,
                title = stringResource(MR.strings.label_library),
                icon = rememberAnimatedVectorPainter(image, isSelected),
            )
        }

    override suspend fun onReselect(navigator: Navigator) {
        when (latestPane) {
            LibraryPane.Manga -> MangaLibraryTab.onReselect(navigator)
            LibraryPane.Anime -> AnimeLibraryTab.onReselect(navigator)
        }
    }

    @Composable
    override fun Content() {
        var currentPane by rememberSaveable { mutableStateOf(latestPane) }

        LaunchedEffect(Unit) {
            switchPaneChannel.receiveAsFlow().collectLatest { pane ->
                latestPane = pane
                currentPane = pane
            }
        }

        when (currentPane) {
            LibraryPane.Manga -> MangaLibraryTab.Content()
            LibraryPane.Anime -> AnimeLibraryTab.Content()
        }
    }

    fun showManga() {
        latestPane = LibraryPane.Manga
        switchPaneChannel.trySend(LibraryPane.Manga)
    }

    fun showAnime() {
        latestPane = LibraryPane.Anime
        switchPaneChannel.trySend(LibraryPane.Anime)
    }

    suspend fun search(query: String) {
        when (latestPane) {
            LibraryPane.Manga -> MangaLibraryTab.search(query)
            LibraryPane.Anime -> AnimeLibraryTab.search(query)
        }
    }

    @Composable
    fun MediaSwitcher(
        selectedPane: LibraryPane,
        modifier: Modifier = Modifier,
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = etsumeGlassContainerColor(0.54f),
            shape = MaterialTheme.shapes.extraLarge,
            border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            ) {
                LibraryPane.entries.forEach { pane ->
                    val selected = pane == selectedPane
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                when (pane) {
                                    LibraryPane.Manga -> showManga()
                                    LibraryPane.Anime -> showAnime()
                                }
                            },
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
                                .then(if (selected) Modifier.background(etsumeAccentBrush(0.95f)) else Modifier)
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = androidStringResource(
                                    if (pane == LibraryPane.Manga) {
                                        R.string.etsume_label_manga
                                    } else {
                                        R.string.etsume_label_anime
                                    },
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
}
