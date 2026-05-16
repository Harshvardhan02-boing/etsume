package eu.kanade.tachiyomi.ui.reader.viewer

import android.content.Context
import android.util.AttributeSet
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import eu.kanade.presentation.reader.ChapterTransition
import eu.kanade.presentation.theme.TachiyomiTheme
import eu.kanade.tachiyomi.data.download.manga.MangaDownloadManager
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import eu.kanade.tachiyomi.ui.reader.model.ChapterTransition
import tachiyomi.domain.entries.manga.model.Manga
import tachiyomi.domain.items.chapter.model.Chapter as DomainChapter
import eu.kanade.tachiyomi.data.database.models.manga.toDomainChapter
import tachiyomi.source.local.entries.manga.isLocal

class ReaderTransitionView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AbstractComposeView(context, attrs) {

    private var data: Data? by mutableStateOf(null)

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    fun bind(
        transition: ChapterTransition,
        downloadManager: MangaDownloadManager,
        manga: Manga?,
        activity: ReaderActivity,
    ) {
        val viewerChapters = activity.viewModel.state.value.viewerChapters
        data = if (manga != null) {
            Data(
                transition = transition,
                currChapterDownloaded = transition.from.pageLoader?.isLocal == true,
                goingToChapterDownloaded = manga.isLocal() ||
                    transition.to?.chapter?.let { goingToChapter ->
                        downloadManager.isChapterDownloaded(
                            chapterName = goingToChapter.name,
                            chapterScanlator = goingToChapter.scanlator,
                            mangaTitle = manga.title,
                            sourceId = manga.source,
                            skipCache = true,
                        )
                    } ?: false,
                previousChapter = viewerChapters?.prevChapter?.chapter?.toDomainChapter(),
                nextChapter = viewerChapters?.nextChapter?.chapter?.toDomainChapter(),
                onOpenPrevious = { activity.loadPreviousChapter() },
                onOpenNext = { activity.loadNextChapter() },
            )
        } else {
            null
        }
    }

    @Composable
    override fun Content() {
        data?.let {
            TachiyomiTheme {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodySmall,
                    LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                ) {
                    ChapterTransition(
                        transition = it.transition,
                        currChapterDownloaded = it.currChapterDownloaded,
                        goingToChapterDownloaded = it.goingToChapterDownloaded,
                        previousChapter = it.previousChapter,
                        nextChapter = it.nextChapter,
                        onOpenPrevious = it.onOpenPrevious,
                        onOpenNext = it.onOpenNext,
                    )
                }
            }
        }
    }
    private data class Data(
        val transition: ChapterTransition,
        val currChapterDownloaded: Boolean,
        val goingToChapterDownloaded: Boolean,
        val previousChapter: DomainChapter?,
        val nextChapter: DomainChapter?,
        val onOpenPrevious: (() -> Unit)?,
        val onOpenNext: (() -> Unit)?,
    )
}
