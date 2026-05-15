package eu.kanade.presentation.browse.manga.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
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
import tachiyomi.domain.items.chapter.interactor.GetChaptersByMangaId
import tachiyomi.domain.entries.manga.model.Manga
import tachiyomi.domain.entries.manga.model.MangaCover
import tachiyomi.presentation.core.util.plus
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun BrowseMangaSourceList(
    mangaList: LazyPagingItems<StateFlow<Manga>>,
    entries: Int,
    topBarHeight: Int,
    contentPadding: PaddingValues,
    onMangaClick: (Manga) -> Unit,
    onMangaLongClick: (Manga) -> Unit,
) {
    val sourceListState = rememberLazyListState()
    LazyColumn(
        state = sourceListState,
        contentPadding = contentPadding + PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            if (mangaList.loadState.prepend is LoadState.Loading) {
                BrowseSourceLoadingItem()
            }
        }
        items(count = mangaList.itemCount) { index ->
            val manga by mangaList[index]?.collectAsState() ?: return@items
            BrowseMangaSourceListItem(manga, { onMangaClick(manga) }, { onMangaLongClick(manga) })
        }
        item {
            if (mangaList.loadState.refresh is LoadState.Loading || mangaList.loadState.append is LoadState.Loading) {
                BrowseSourceLoadingItem()
            }
        }
    }
}

@Composable
private fun BrowseMangaSourceListItem(manga: Manga, onClick: () -> Unit, onLongClick: () -> Unit) {
    val totalChapters = rememberLocalMangaChapterCount(manga.id)
    val primaryMetadata = remember(manga.author, manga.artist, manga.genre) {
        manga.author
            ?.takeIf { it.isNotBlank() }
            ?: manga.artist?.takeIf { it.isNotBlank() }
            ?: manga.genre?.firstOrNull()?.takeIf { it.isNotBlank() }
    }
    BaseBrowseItem(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClickItem = onClick,
        onLongClickItem = onLongClick,
        icon = {
            Box {
                ItemCover.Book(
                    data = MangaCover(manga.id, manga.source, manga.favorite, manga.thumbnailUrl, manga.coverLastModified),
                    modifier = Modifier.size(width = 72.dp, height = 104.dp),
                    shape = MaterialTheme.shapes.large,
                )
                InLibraryBadge(enabled = manga.favorite, modifier = Modifier.align(Alignment.TopStart))
            }
        },
        content = {
            Column(
                modifier = Modifier.weight(1f).padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(manga.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (primaryMetadata != null) {
                    Text(
                        primaryMetadata,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (totalChapters != null) {
                    Text(
                        chapterCountLabel(totalChapters),
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
private fun rememberLocalMangaChapterCount(mangaId: Long): Int? {
    val getChaptersByMangaId = remember { Injekt.get<GetChaptersByMangaId>() }
    return produceState<Int?>(initialValue = null, key1 = mangaId) {
        value = withContext(Dispatchers.IO) {
            getChaptersByMangaId.await(mangaId, applyScanlatorFilter = true)
                .takeIf { it.isNotEmpty() }
                ?.size
        }
    }.value
}

private fun chapterCountLabel(count: Int): String {
    return if (count == 1) "$count chapter" else "$count chapters"
}
