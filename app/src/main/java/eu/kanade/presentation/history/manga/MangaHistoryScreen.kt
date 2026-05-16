package eu.kanade.presentation.history.manga

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import eu.kanade.domain.base.BasePreferences
import eu.kanade.tachiyomi.extension.manga.MangaExtensionManager
import eu.kanade.presentation.components.relativeDateText
import eu.kanade.presentation.history.manga.components.MangaHistoryItem
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.util.animateItemFastScroll
import eu.kanade.presentation.util.isBlockedExplicitContent
import eu.kanade.tachiyomi.ui.history.manga.MangaHistoryScreenModel
import tachiyomi.domain.history.manga.model.MangaHistoryWithRelations
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.FastScrollLazyColumn
import tachiyomi.presentation.core.components.ListGroupHeader
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen
import java.time.LocalDate
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun MangaHistoryScreen(
    state: MangaHistoryScreenModel.State,
    snackbarHostState: SnackbarHostState,
    onClickCover: (mangaId: Long) -> Unit,
    onClickResume: (mangaId: Long, chapterId: Long) -> Unit,
    onClickFavorite: (mangaId: Long) -> Unit,
    onDialogChange: (MangaHistoryScreenModel.Dialog?) -> Unit,
    searchQuery: String? = null,
) {
    val basePreferences: BasePreferences = Injekt.get()
    val mangaExtensionManager: MangaExtensionManager = Injekt.get()
    val showNsfw by basePreferences.showExplicitTitles().changes().collectAsState(initial = basePreferences.showExplicitTitles().get())

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        val filteredHistory = state.list?.filterVisible(showNsfw, mangaExtensionManager)
        when {
            filteredHistory == null -> LoadingScreen(Modifier.padding(contentPadding))
            filteredHistory.isEmpty() -> {
                val msg = if (!searchQuery.isNullOrEmpty()) MR.strings.no_results_found else MR.strings.information_no_recent_manga
                EmptyScreen(stringRes = msg, modifier = Modifier.padding(contentPadding))
            }
            else -> {
                MangaHistoryScreenContent(
                    history = filteredHistory,
                    contentPadding = contentPadding,
                    onClickCover = { history -> onClickCover(history.mangaId) },
                    onClickResume = { history -> onClickResume(history.mangaId, history.chapterId) },
                    onClickDelete = { item -> onDialogChange(MangaHistoryScreenModel.Dialog.Delete(item)) },
                    onClickFavorite = { history -> onClickFavorite(history.mangaId) },
                )
            }
        }
    }
}

private fun List<MangaHistoryUiModel>.filterVisible(showNsfw: Boolean, mangaExtensionManager: MangaExtensionManager): List<MangaHistoryUiModel> {
    if (showNsfw) return this
    val filtered = mutableListOf<MangaHistoryUiModel>()
    var pendingHeader: MangaHistoryUiModel.Header? = null
    forEach { item ->
        when (item) {
            is MangaHistoryUiModel.Header -> pendingHeader = item
            is MangaHistoryUiModel.Item -> {
                if (!isBlockedExplicitContent(showNsfw = showNsfw, sourceId = item.item.coverData.sourceId, mangaExtensionManager = mangaExtensionManager, fields = listOf(item.item.title))) {
                    pendingHeader?.let(filtered::add)
                    pendingHeader = null
                    filtered += item
                }
            }
        }
    }
    return filtered
}

@Composable
private fun MangaHistoryScreenContent(
    history: List<MangaHistoryUiModel>,
    contentPadding: PaddingValues,
    onClickCover: (MangaHistoryWithRelations) -> Unit,
    onClickResume: (MangaHistoryWithRelations) -> Unit,
    onClickDelete: (MangaHistoryWithRelations) -> Unit,
    onClickFavorite: (MangaHistoryWithRelations) -> Unit,
) {
    FastScrollLazyColumn(contentPadding = contentPadding) {
        items(
            items = history,
            key = { "history-${it.hashCode()}" },
            contentType = {
                when (it) {
                    is MangaHistoryUiModel.Header -> "header"
                    is MangaHistoryUiModel.Item -> "item"
                }
            },
        ) { item ->
            when (item) {
                is MangaHistoryUiModel.Header -> ListGroupHeader(
                    modifier = Modifier.animateItemFastScroll(),
                    text = relativeDateText(item.date),
                )
                is MangaHistoryUiModel.Item -> {
                    val value = item.item
                    MangaHistoryItem(
                        modifier = Modifier.animateItemFastScroll(),
                        history = value,
                        onClickCover = { onClickCover(value) },
                        onClickResume = { onClickResume(value) },
                        onClickDelete = { onClickDelete(value) },
                        onClickFavorite = { onClickFavorite(value) },
                    )
                }
            }
        }
    }
}

sealed interface MangaHistoryUiModel {
    data class Header(val date: LocalDate) : MangaHistoryUiModel
    data class Item(val item: MangaHistoryWithRelations) : MangaHistoryUiModel
}

@PreviewLightDark
@Composable
internal fun HistoryScreenPreviews(
    @PreviewParameter(MangaHistoryScreenModelStateProvider::class)
    historyState: MangaHistoryScreenModel.State,
) {
    TachiyomiPreviewTheme {
        MangaHistoryScreen(
            state = historyState,
            snackbarHostState = SnackbarHostState(),
            searchQuery = null,
            onClickCover = {},
            onClickResume = { _, _ -> run {} },
            onDialogChange = {},
            onClickFavorite = {},
        )
    }
}
