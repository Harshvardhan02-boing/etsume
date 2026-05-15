package eu.kanade.presentation.library.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import eu.kanade.presentation.components.EtsumeMediaGridCard
import eu.kanade.presentation.library.components.globalSearchItem
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.library.anime.AnimeLibraryItem
import tachiyomi.domain.entries.anime.model.AnimeCover
import tachiyomi.domain.library.anime.LibraryAnime
import tachiyomi.presentation.core.components.FastScrollLazyVerticalGrid
import tachiyomi.presentation.core.util.plus

@Composable
fun AnimeLibraryCompactGrid(
    items: List<AnimeLibraryItem>,
    showTitle: Boolean,
    columns: Int,
    contentPadding: PaddingValues,
    selection: List<LibraryAnime>,
    onClick: (LibraryAnime) -> Unit,
    onLongClick: (LibraryAnime) -> Unit,
    onClickContinueWatching: ((LibraryAnime) -> Unit)?,
    searchQuery: String?,
    onGlobalSearchClicked: () -> Unit,
) {
    FastScrollLazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = contentPadding + PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        globalSearchItem(searchQuery, onGlobalSearchClicked)

        items(
            items = items,
            contentType = { "anime_library_compact_grid_item" },
        ) { libraryItem ->
            val anime = libraryItem.libraryAnime.anime
            val isSelected = selection.fastAny { it.id == libraryItem.libraryAnime.id }
            val hasStarted = libraryItem.libraryAnime.hasStarted
            val onActionClick = if (hasStarted && onClickContinueWatching != null) {
                { onClickContinueWatching(libraryItem.libraryAnime) }
            } else {
                { onClick(libraryItem.libraryAnime) }
            }
            EtsumeMediaGridCard(
                title = anime.title,
                subtitle = episodeCountLabel(libraryItem.libraryAnime.totalCount),
                coverData = AnimeCover(
                    animeId = anime.id,
                    sourceId = anime.source,
                    isAnimeFavorite = anime.favorite,
                    url = anime.thumbnailUrl,
                    lastModified = anime.coverLastModified,
                ),
                actionLabel = if (hasStarted && onClickContinueWatching != null) {
                    stringResource(R.string.etsume_action_continue)
                } else {
                    stringResource(R.string.etsume_action_open)
                },
                onClick = { onClick(libraryItem.libraryAnime) },
                onLongClick = { onLongClick(libraryItem.libraryAnime) },
                onActionClick = onActionClick,
                isSelected = isSelected,
            )
        }
    }
}

private fun episodeCountLabel(count: Long): String {
    return if (count == 1L) "$count episode" else "$count episodes"
}
