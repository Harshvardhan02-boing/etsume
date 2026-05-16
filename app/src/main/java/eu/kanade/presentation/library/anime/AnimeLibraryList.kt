package eu.kanade.presentation.library.anime

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import eu.kanade.presentation.components.EtsumeMediaListCard
import eu.kanade.presentation.library.components.GlobalSearchItem
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.library.anime.AnimeLibraryItem
import tachiyomi.domain.entries.anime.model.AnimeCover
import tachiyomi.domain.library.anime.LibraryAnime
import tachiyomi.presentation.core.components.FastScrollLazyColumn
import tachiyomi.presentation.core.util.plus

@Composable
internal fun AnimeLibraryList(
    items: List<AnimeLibraryItem>,
    entries: Int,
    containerHeight: Int,
    contentPadding: PaddingValues,
    selection: List<LibraryAnime>,
    onClick: (LibraryAnime) -> Unit,
    onLongClick: (LibraryAnime) -> Unit,
    onClickContinueWatching: ((LibraryAnime) -> Unit)?,
    searchQuery: String?,
    onGlobalSearchClicked: () -> Unit,
) {
    FastScrollLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(vertical = 8.dp),
    ) {
        item {
            if (!searchQuery.isNullOrEmpty()) {
                GlobalSearchItem(
                    modifier = Modifier.fillMaxWidth(),
                    searchQuery = searchQuery,
                    onClick = onGlobalSearchClicked,
                )
            }
        }

        items(
            items = items,
            contentType = { "anime_library_list_item" },
        ) { libraryItem ->
            val anime = libraryItem.libraryAnime.anime
            val hasStarted = libraryItem.libraryAnime.hasStarted
            EtsumeMediaListCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                isSelected = selection.fastAny { it.id == libraryItem.libraryAnime.id },
                title = anime.title,
                subtitle = episodeCountLabel(libraryItem.libraryAnime.totalCount),
                coverData = AnimeCover(
                    animeId = anime.id,
                    sourceId = anime.source,
                    isAnimeFavorite = anime.favorite,
                    url = anime.thumbnailUrl,
                    lastModified = anime.coverLastModified,
                ),
                onClick = { onClick(libraryItem.libraryAnime) },
                onLongClick = { onLongClick(libraryItem.libraryAnime) },
                actionLabel = if (hasStarted && onClickContinueWatching != null) {
                    stringResource(R.string.etsume_action_continue)
                } else {
                    stringResource(R.string.etsume_action_open)
                },
                onActionClick = if (hasStarted && onClickContinueWatching != null) {
                    { onClickContinueWatching(libraryItem.libraryAnime) }
                } else {
                    { onClick(libraryItem.libraryAnime) }
                },
            )
        }
    }
}

private fun episodeCountLabel(count: Long): String {
    return if (count == 1L) "$count episode" else "$count episodes"
}
