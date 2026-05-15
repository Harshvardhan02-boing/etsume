package eu.kanade.presentation.browse.anime.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import eu.kanade.presentation.browse.BaseBrowseItem
import eu.kanade.presentation.browse.BrowseActionChip
import eu.kanade.presentation.browse.BrowseSourceLoadingItem
import eu.kanade.presentation.browse.InLibraryBadge
import eu.kanade.presentation.entries.components.ItemCover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import tachiyomi.domain.items.episode.interactor.GetEpisodesByAnimeId
import tachiyomi.domain.entries.anime.model.Anime
import tachiyomi.domain.entries.anime.model.AnimeCover
import tachiyomi.presentation.core.util.plus
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun BrowseAnimeSourceList(
    animeList: LazyPagingItems<StateFlow<Anime>>,
    entries: Int,
    topBarHeight: Int,
    contentPadding: PaddingValues,
    onAnimeClick: (Anime) -> Unit,
    onAnimeLongClick: (Anime) -> Unit,
) {
    val sourceListState = rememberLazyListState()
    LazyColumn(
        state = sourceListState,
        contentPadding = contentPadding + PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            if (animeList.loadState.prepend is LoadState.Loading) {
                BrowseSourceLoadingItem()
            }
        }
        items(count = animeList.itemCount) { index ->
            val anime by animeList[index]?.collectAsState() ?: return@items
            BrowseAnimeSourceListItem(anime, { onAnimeClick(anime) }, { onAnimeLongClick(anime) })
        }
        item {
            if (animeList.loadState.refresh is LoadState.Loading || animeList.loadState.append is LoadState.Loading) {
                BrowseSourceLoadingItem()
            }
        }
    }
}

@Composable
private fun BrowseAnimeSourceListItem(anime: Anime, onClick: () -> Unit, onLongClick: () -> Unit) {
    val totalEpisodes = rememberLocalAnimeEpisodeCount(anime.id)
    val primaryMetadata = remember(anime.author, anime.artist, anime.genre) {
        anime.author
            ?.takeIf { it.isNotBlank() }
            ?: anime.artist?.takeIf { it.isNotBlank() }
            ?: anime.genre?.firstOrNull()?.takeIf { it.isNotBlank() }
    }
    BaseBrowseItem(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClickItem = onClick,
        onLongClickItem = onLongClick,
        icon = {
            Box {
                ItemCover.Book(
                    data = AnimeCover(anime.id, anime.source, anime.favorite, anime.thumbnailUrl, anime.coverLastModified),
                    modifier = Modifier.size(width = 72.dp, height = 104.dp),
                    shape = MaterialTheme.shapes.large,
                )
                InLibraryBadge(enabled = anime.favorite, modifier = Modifier.align(Alignment.TopStart))
            }
        },
        content = {
            Column(
                modifier = Modifier.weight(1f).padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(anime.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (primaryMetadata != null) {
                    Text(
                        primaryMetadata,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (totalEpisodes != null) {
                    Text(
                        episodeCountLabel(totalEpisodes),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        action = {
            BrowseActionChip(label = "Open", onClick = onClick, modifier = Modifier.width(86.dp))
        },
    )
}

@Composable
private fun rememberLocalAnimeEpisodeCount(animeId: Long): Int? {
    val getEpisodesByAnimeId = remember { Injekt.get<GetEpisodesByAnimeId>() }
    return produceState<Int?>(initialValue = null, key1 = animeId) {
        value = withContext(Dispatchers.IO) {
            getEpisodesByAnimeId.await(animeId)
                .takeIf { it.isNotEmpty() }
                ?.size
        }
    }.value
}

private fun episodeCountLabel(count: Int): String {
    return if (count == 1) "$count episode" else "$count episodes"
}
