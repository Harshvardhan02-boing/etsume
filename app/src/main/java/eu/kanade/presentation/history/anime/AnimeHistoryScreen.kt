package eu.kanade.presentation.history.anime

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
import eu.kanade.tachiyomi.extension.anime.AnimeExtensionManager
import eu.kanade.presentation.components.relativeDateText
import eu.kanade.presentation.history.anime.components.AnimeHistoryItem
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.util.animateItemFastScroll
import eu.kanade.presentation.util.isBlockedExplicitContent
import eu.kanade.tachiyomi.ui.history.anime.AnimeHistoryScreenModel
import tachiyomi.domain.history.anime.model.AnimeHistoryWithRelations
import tachiyomi.i18n.MR
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.presentation.core.components.FastScrollLazyColumn
import tachiyomi.presentation.core.components.ListGroupHeader
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen
import java.time.LocalDate
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun AnimeHistoryScreen(
    state: AnimeHistoryScreenModel.State,
    snackbarHostState: SnackbarHostState,
    onClickCover: (animeId: Long) -> Unit,
    onClickResume: (animeId: Long, episodeId: Long) -> Unit,
    onClickFavorite: (animeId: Long) -> Unit,
    onDialogChange: (AnimeHistoryScreenModel.Dialog?) -> Unit,
    searchQuery: String? = null,
) {
    val basePreferences: BasePreferences = Injekt.get()
    val animeExtensionManager: AnimeExtensionManager = Injekt.get()
    val showNsfw by basePreferences.showExplicitTitles().changes().collectAsState(initial = basePreferences.showExplicitTitles().get())

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        val filteredHistory = state.list?.filterVisible(showNsfw, animeExtensionManager)
        when {
            filteredHistory == null -> LoadingScreen(Modifier.padding(contentPadding))
            filteredHistory.isEmpty() -> {
                val msg = if (!searchQuery.isNullOrEmpty()) MR.strings.no_results_found else AYMR.strings.information_no_recent_anime
                EmptyScreen(stringRes = msg, modifier = Modifier.padding(contentPadding))
            }
            else -> {
                AnimeHistoryScreenContent(
                    history = filteredHistory,
                    contentPadding = contentPadding,
                    onClickCover = { history -> onClickCover(history.animeId) },
                    onClickResume = { history -> onClickResume(history.animeId, history.episodeId) },
                    onClickDelete = { item -> onDialogChange(AnimeHistoryScreenModel.Dialog.Delete(item)) },
                    onClickFavorite = { history -> onClickFavorite(history.animeId) },
                )
            }
        }
    }
}

private fun List<AnimeHistoryUiModel>.filterVisible(showNsfw: Boolean, animeExtensionManager: AnimeExtensionManager): List<AnimeHistoryUiModel> {
    if (showNsfw) return this
    val filtered = mutableListOf<AnimeHistoryUiModel>()
    var pendingHeader: AnimeHistoryUiModel.Header? = null
    forEach { item ->
        when (item) {
            is AnimeHistoryUiModel.Header -> pendingHeader = item
            is AnimeHistoryUiModel.Item -> {
                if (!isBlockedExplicitContent(showNsfw = showNsfw, sourceId = item.item.coverData.sourceId, animeExtensionManager = animeExtensionManager, fields = listOf(item.item.title))) {
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
private fun AnimeHistoryScreenContent(
    history: List<AnimeHistoryUiModel>,
    contentPadding: PaddingValues,
    onClickCover: (AnimeHistoryWithRelations) -> Unit,
    onClickResume: (AnimeHistoryWithRelations) -> Unit,
    onClickDelete: (AnimeHistoryWithRelations) -> Unit,
    onClickFavorite: (AnimeHistoryWithRelations) -> Unit,
) {
    FastScrollLazyColumn(contentPadding = contentPadding) {
        items(
            items = history,
            key = { "history-${it.hashCode()}" },
            contentType = {
                when (it) {
                    is AnimeHistoryUiModel.Header -> "header"
                    is AnimeHistoryUiModel.Item -> "item"
                }
            },
        ) { item ->
            when (item) {
                is AnimeHistoryUiModel.Header -> ListGroupHeader(
                    modifier = Modifier.animateItemFastScroll(),
                    text = relativeDateText(item.date),
                )
                is AnimeHistoryUiModel.Item -> {
                    val value = item.item
                    AnimeHistoryItem(
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

sealed interface AnimeHistoryUiModel {
    data class Header(val date: LocalDate) : AnimeHistoryUiModel
    data class Item(val item: AnimeHistoryWithRelations) : AnimeHistoryUiModel
}

@PreviewLightDark
@Composable
internal fun HistoryScreenPreviews(
    @PreviewParameter(AnimeHistoryScreenModelStateProvider::class)
    historyState: AnimeHistoryScreenModel.State,
) {
    TachiyomiPreviewTheme {
        AnimeHistoryScreen(
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
