package eu.kanade.presentation.browse.anime.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import eu.kanade.presentation.browse.BrowseSourceLoadingItem
import eu.kanade.presentation.browse.InLibraryBadge
import eu.kanade.presentation.entries.components.ItemCover
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import kotlinx.coroutines.flow.StateFlow
import tachiyomi.domain.entries.anime.model.Anime
import tachiyomi.domain.entries.anime.model.AnimeCover
import tachiyomi.presentation.core.util.plus

@Composable
fun BrowseAnimeSourceComfortableGrid(
    animeList: LazyPagingItems<StateFlow<Anime>>,
    columns: GridCells,
    contentPadding: PaddingValues,
    onAnimeClick: (Anime) -> Unit,
    onAnimeLongClick: (Anime) -> Unit,
) {
    val items = animeList
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = contentPadding + PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (items.loadState.prepend is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) { BrowseSourceLoadingItem() }
        }
        items(count = items.itemCount) { index ->
            val entry by items[index]?.collectAsState() ?: return@items
            BrowseAnimeGridCard(entry, { onAnimeClick(entry) }, { onAnimeLongClick(entry) })
        }
        if (items.loadState.refresh is LoadState.Loading || items.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) { BrowseSourceLoadingItem() }
        }
    }
}

@Composable
private fun BrowseAnimeGridCard(entry: Anime, onClick: () -> Unit, onLongClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = etsumeGlassContainerColor(0.52f),
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.2f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box {
                ItemCover.Book(
                    data = AnimeCover(animeId = entry.id, sourceId = entry.source, isAnimeFavorite = entry.favorite, url = entry.thumbnailUrl, lastModified = entry.coverLastModified),
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = MaterialTheme.shapes.large,
                )
                InLibraryBadge(enabled = entry.favorite, modifier = Modifier.align(Alignment.TopStart))
            }
            Text(entry.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp))
            Box(
                modifier = Modifier.clip(MaterialTheme.shapes.large).background(etsumeAccentBrush(0.94f)).fillMaxWidth().padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Open", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
