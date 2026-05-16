package eu.kanade.presentation.library.manga

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
import eu.kanade.tachiyomi.ui.library.manga.MangaLibraryItem
import tachiyomi.domain.entries.manga.model.MangaCover
import tachiyomi.domain.library.manga.LibraryManga
import tachiyomi.presentation.core.components.FastScrollLazyVerticalGrid
import tachiyomi.presentation.core.util.plus

@Composable
internal fun MangaLibraryCompactGrid(
    items: List<MangaLibraryItem>,
    showTitle: Boolean,
    columns: Int,
    contentPadding: PaddingValues,
    selection: List<LibraryManga>,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
    onClickContinueReading: ((LibraryManga) -> Unit)?,
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
            contentType = { "manga_library_compact_grid_item" },
        ) { libraryItem ->
            val manga = libraryItem.libraryManga.manga
            val isSelected = selection.fastAny { it.id == libraryItem.libraryManga.id }
            val hasStarted = libraryItem.libraryManga.hasStarted
            val onActionClick = if (hasStarted && onClickContinueReading != null) {
                { onClickContinueReading(libraryItem.libraryManga) }
            } else {
                { onClick(libraryItem.libraryManga) }
            }
            EtsumeMediaGridCard(
                title = manga.title,
                subtitle = chapterCountLabel(libraryItem.libraryManga.totalChapters),
                coverData = MangaCover(
                    mangaId = manga.id,
                    sourceId = manga.source,
                    isMangaFavorite = manga.favorite,
                    url = manga.thumbnailUrl,
                    lastModified = manga.coverLastModified,
                ),
                actionLabel = if (hasStarted && onClickContinueReading != null) {
                    stringResource(R.string.etsume_action_continue)
                } else {
                    stringResource(R.string.etsume_action_open)
                },
                onClick = { onClick(libraryItem.libraryManga) },
                onLongClick = { onLongClick(libraryItem.libraryManga) },
                onActionClick = onActionClick,
                isSelected = isSelected,
            )
        }
    }
}

private fun chapterCountLabel(count: Long): String {
    return if (count == 1L) "$count chapter" else "$count chapters"
}
