package eu.kanade.presentation.library.manga

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
import eu.kanade.tachiyomi.ui.library.manga.MangaLibraryItem
import tachiyomi.domain.entries.manga.model.MangaCover
import tachiyomi.domain.library.manga.LibraryManga
import tachiyomi.presentation.core.components.FastScrollLazyColumn
import tachiyomi.presentation.core.util.plus

@Composable
internal fun MangaLibraryList(
    items: List<MangaLibraryItem>,
    entries: Int,
    containerHeight: Int,
    contentPadding: PaddingValues,
    selection: List<LibraryManga>,
    onClick: (LibraryManga) -> Unit,
    onLongClick: (LibraryManga) -> Unit,
    onClickContinueReading: ((LibraryManga) -> Unit)?,
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
            contentType = { "manga_library_list_item" },
        ) { libraryItem ->
            val manga = libraryItem.libraryManga.manga
            val hasStarted = libraryItem.libraryManga.hasStarted
            EtsumeMediaListCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                isSelected = selection.fastAny { it.id == libraryItem.libraryManga.id },
                title = manga.title,
                subtitle = chapterCountLabel(libraryItem.libraryManga.totalChapters),
                coverData = MangaCover(
                    mangaId = manga.id,
                    sourceId = manga.source,
                    isMangaFavorite = manga.favorite,
                    url = manga.thumbnailUrl,
                    lastModified = manga.coverLastModified,
                ),
                onLongClick = { onLongClick(libraryItem.libraryManga) },
                onClick = { onClick(libraryItem.libraryManga) },
                actionLabel = if (hasStarted && onClickContinueReading != null) {
                    stringResource(R.string.etsume_action_continue)
                } else {
                    stringResource(R.string.etsume_action_open)
                },
                onActionClick = if (hasStarted && onClickContinueReading != null) {
                    { onClickContinueReading(libraryItem.libraryManga) }
                } else {
                    { onClick(libraryItem.libraryManga) }
                },
            )
        }
    }
}

private fun chapterCountLabel(count: Long): String {
    return if (count == 1L) "$count chapter" else "$count chapters"
}
