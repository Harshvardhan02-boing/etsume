package eu.kanade.tachiyomi.ui.player.controls

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.vivvvek.seeker.Segment
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults
import eu.kanade.presentation.comments.EtsumeCommentPlaceholder
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.animesource.AnimeSource
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import eu.kanade.tachiyomi.data.database.models.anime.Episode
import eu.kanade.tachiyomi.ui.player.PlayerActivity
import eu.kanade.tachiyomi.ui.player.PlayerViewModel
import eu.kanade.tachiyomi.ui.player.Sheets
import eu.kanade.tachiyomi.util.system.toast
import `is`.xyz.mpv.Utils
import kotlinx.collections.immutable.toImmutableList
import tachiyomi.domain.entries.anime.model.Anime
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun EtsumeLandscapePlayerChrome(
    viewModel: PlayerViewModel,
    onBackPress: () -> Unit,
    seekBarShown: Boolean,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as? PlayerActivity
    val controlsShown by viewModel.controlsShown.collectAsState()
    val paused by viewModel.paused.collectAsState()
    val hasPrevious by viewModel.hasPreviousEpisode.collectAsState()
    val hasNext by viewModel.hasNextEpisode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingEpisode by viewModel.isLoadingEpisode.collectAsState()
    val position by viewModel.pos.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val readAhead by viewModel.readAhead.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val animeTitle by viewModel.animeTitle.collectAsState()
    val mediaTitle by viewModel.mediaTitle.collectAsState()
    val currentAnime by viewModel.currentAnime.collectAsState()
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    AnimatedVisibility(
        visible = controlsShown || seekBarShown,
        modifier = modifier,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 12.dp, end = 12.dp),
            ) {
                val titleBarMaxWidth = maxWidth * 0.6f
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EtsumePlayerTitleBar(
                        animeTitle = animeTitle,
                        mediaTitle = mediaTitle,
                        onBackPress = onBackPress,
                        modifier = Modifier.widthIn(max = titleBarMaxWidth),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PlayerGlassIconButton(
                            icon = if (currentAnime?.favorite == true) Icons.Default.Bookmarks else Icons.Outlined.BookmarkAdd,
                            label = androidStringResource(R.string.etsume_action_library),
                            onClick = viewModel::toggleLibrary,
                            tinted = currentAnime?.favorite == true,
                        )
                        PlayerGlassIconButton(
                            icon = Icons.Default.ChatBubbleOutline,
                            label = androidStringResource(R.string.etsume_comments_title),
                            onClick = { viewModel.showSheet(Sheets.Comments) },
                        )
                        PlayerGlassIconButton(
                            icon = Icons.Default.Settings,
                            label = androidStringResource(R.string.etsume_player_settings),
                            onClick = { viewModel.showSheet(Sheets.More) },
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, bottom = navigationPadding + 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LandscapeTimelineStrip(
                    position = position,
                    duration = duration,
                    readAhead = readAhead,
                    chapters = chapters.map { it.toSegment() }.toImmutableList(),
                    onSeek = { viewModel.updatePlayBackPos(it); viewModel.seekTo(it.toInt()) },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PlayerGlassIconButton(
                            icon = Icons.Default.Subtitles,
                            label = androidStringResource(R.string.etsume_player_subtitles),
                            onClick = { viewModel.showSheet(Sheets.SubtitleTracks) },
                        )
                        PlayerGlassIconButton(
                            icon = Icons.Default.Audiotrack,
                            label = androidStringResource(R.string.etsume_player_audio),
                            onClick = { viewModel.showSheet(Sheets.AudioTracks) },
                        )
                        PlayerGlassIconButton(
                            icon = Icons.Default.HighQuality,
                            label = androidStringResource(R.string.etsume_player_quality),
                            onClick = { viewModel.showSheet(Sheets.QualityTracks) },
                        )
                    }
                    PlayerTransportCluster(
                        paused = paused,
                        hasPrevious = hasPrevious,
                        hasNext = hasNext,
                        onPrevious = { viewModel.changeEpisode(true) },
                        onPlayPause = viewModel::pauseUnpause,
                        onNext = { viewModel.changeEpisode(false) },
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PlayerGlassIconButton(
                            icon = Icons.AutoMirrored.Filled.ViewList,
                            label = androidStringResource(R.string.etsume_player_episode_list),
                            onClick = viewModel::showEpisodeListDialog,
                        )
                        if (activity?.isPipSupportedAndEnabled == true) {
                            PlayerGlassIconButton(
                                icon = Icons.Default.PictureInPictureAlt,
                                label = "PiP",
                                onClick = activity::enterPipIfPossible,
                            )
                        }
                        PlayerGlassIconButton(
                            icon = Icons.Default.FullscreenExit,
                            label = androidStringResource(R.string.etsume_player_minimize),
                            onClick = { activity?.showPortraitPlayer() ?: viewModel.cycleScreenRotations() },
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun EtsumePortraitPlayerChrome(
    viewModel: PlayerViewModel,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as? PlayerActivity

    val controlsShown by viewModel.controlsShown.collectAsState()
    val paused by viewModel.paused.collectAsState()
    val hasPrevious by viewModel.hasPreviousEpisode.collectAsState()
    val hasNext by viewModel.hasNextEpisode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingEpisode by viewModel.isLoadingEpisode.collectAsState()
    val position by viewModel.pos.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val readAhead by viewModel.readAhead.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val animeTitle by viewModel.animeTitle.collectAsState()
    val mediaTitle by viewModel.mediaTitle.collectAsState()
    val playlist by viewModel.currentPlaylist.collectAsState()
    val currentEpisode by viewModel.currentEpisode.collectAsState()
    val currentAnime by viewModel.currentAnime.collectAsState()
    val currentSource by viewModel.currentSource.collectAsState()
    val density = LocalDensity.current
    val statusPadding = remember(activity, density) {
        with(density) {
            (activity?.currentStatusBarInset() ?: 0).toDp()
        }
    }
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var commentsExpanded by rememberSaveable { mutableStateOf(false) }
    var pendingEpisodeId by rememberSaveable { mutableStateOf<Long?>(null) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val headerItemCount = 4
        val targetVideoHeight = maxWidth * 0.5625f
        val maxVideoHeight = maxHeight * 0.42f
        val reservedVideoHeight = if (targetVideoHeight < maxVideoHeight) targetVideoHeight else maxVideoHeight
        val currentEpisodeIndex = remember(playlist, currentEpisode) {
            playlist.indexOfFirst { sameEpisode(it, currentEpisode) }
                .takeIf { it >= 0 }
                ?: 0
        }
        val episodeListState = rememberLazyListState(
            initialFirstVisibleItemIndex = if (playlist.isEmpty()) 0 else (currentEpisodeIndex + headerItemCount).coerceAtLeast(0),
        )
        val coroutineScope = rememberCoroutineScope()
        var lastAnchoredEpisodeIndex by rememberSaveable { mutableIntStateOf(-1) }

        LaunchedEffect(currentEpisodeIndex, playlist.size, currentEpisode?.name, currentEpisode?.episode_number) {
            if (playlist.isNotEmpty() && currentEpisodeIndex >= 0 && currentEpisodeIndex != lastAnchoredEpisodeIndex) {
                episodeListState.scrollToItem((currentEpisodeIndex + headerItemCount).coerceAtLeast(0))
                lastAnchoredEpisodeIndex = currentEpisodeIndex
            }
        }

        LaunchedEffect(currentEpisode?.id) {
            if (currentEpisode?.id != null && currentEpisode?.id == pendingEpisodeId) {
                pendingEpisodeId = null
            }
        }

        LaunchedEffect(activity, controlsShown) {
            activity?.refreshSystemUiForCurrentOrientation()
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(reservedVideoHeight)
            ) {
                if (controlsShown) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(horizontal = 12.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PlayerGlassIconButton(
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                label = androidStringResource(R.string.etsume_action_back),
                                onClick = onBackPress,
                            )
                            PlayerGlassIconButton(
                                icon = Icons.Default.MoreVert,
                                label = androidStringResource(R.string.etsume_player_settings),
                                onClick = { viewModel.showSheet(Sheets.More) },
                            )
                        }

                        PlayerTransportCluster(
                            paused = paused,
                            hasPrevious = hasPrevious,
                            hasNext = hasNext,
                            showLoadingIndicator = isLoading || isLoadingEpisode,
                            onPrevious = { viewModel.changeEpisode(true) },
                            onPlayPause = viewModel::pauseUnpause,
                            onNext = { viewModel.changeEpisode(false) },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 18.dp, vertical = 10.dp),
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PortraitTimelineStrip(
                                modifier = Modifier.weight(1f),
                                position = position,
                                duration = duration,
                                readAhead = readAhead,
                                chapters = chapters.map { it.toSegment() }.toImmutableList(),
                                onSeek = { viewModel.updatePlayBackPos(it); viewModel.seekTo(it.toInt()) },
                            )
                            PlayerGlassIconButton(
                                icon = Icons.Default.Fullscreen,
                                label = androidStringResource(R.string.etsume_player_maximize),
                                onClick = { activity?.showLandscapePlayer() ?: viewModel.cycleScreenRotations() },
                                size = 40.dp,
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(0.dp),
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(0.dp, Color.Transparent),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = episodeListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 14.dp,
                            top = 16.dp,
                            end = 14.dp,
                            bottom = 72.dp + navigationPadding,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item(key = "header_title") {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = mediaTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = animeTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        item(key = "header_actions") {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                PlayerActionChip(
                                    icon = if (currentAnime?.favorite == true) Icons.Default.Bookmarks else Icons.Outlined.BookmarkAdd,
                                    label = if (currentAnime?.favorite == true) {
                                        androidStringResource(R.string.etsume_player_in_library)
                                    } else {
                                        androidStringResource(R.string.etsume_action_library)
                                    },
                                    emphasized = currentAnime?.favorite == true,
                                    onClick = viewModel::toggleLibrary,
                                )
                                PlayerActionChip(
                                    icon = Icons.Default.Download,
                                    label = androidStringResource(R.string.etsume_action_download),
                                    onClick = viewModel::downloadCurrentEpisode,
                                )
                                rememberSourceUrl(currentAnime, currentSource)?.let { sourceUrl ->
                                    PlayerActionChip(
                                        icon = Icons.AutoMirrored.Outlined.OpenInNew,
                                        label = androidStringResource(R.string.etsume_action_source),
                                        onClick = { openExternalUrl(context, sourceUrl) },
                                    )
                                }
                            }
                        }

                        item(key = "header_comments") {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.extraLarge,
                                color = etsumeGlassContainerColor(0.46f),
                                border = BorderStroke(1.dp, etsumeGlassBorderColor(0.22f)),
                                tonalElevation = 0.dp,
                                shadowElevation = 0.dp,
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = androidStringResource(R.string.etsume_comment_hint),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = if (commentsExpanded) {
                                                androidStringResource(R.string.etsume_action_show_less)
                                            } else {
                                                androidStringResource(R.string.etsume_action_show_more)
                                            },
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable { commentsExpanded = !commentsExpanded },
                                        )
                                    }
                                    AnimatedVisibility(visible = commentsExpanded) {
                                        EtsumeCommentPlaceholder(
                                            draftKey = "player_${currentAnime?.id ?: 0L}_${currentEpisode?.id ?: 0L}",
                                            showTitle = false,
                                            compact = true,
                                        )
                                    }
                                }
                            }
                        }

                        item(key = "header_episodes") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = androidStringResource(R.string.etsume_player_episode_list),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }

                        items(
                            items = playlist,
                            key = { it.id ?: "${it.url}_${it.name}" },
                        ) { episode ->
                            PortraitEpisodeCard(
                                episode = episode,
                                fallbackThumbnailUrl = currentAnime?.thumbnailUrl,
                                isCurrent = sameEpisode(episode, currentEpisode) || (pendingEpisodeId != null && episode.id == pendingEpisodeId),
                                onOpen = {
                                    pendingEpisodeId = episode.id
                                    activity?.changeEpisode(episode.id)
                                },
                            )
                        }
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = episodeListState.firstVisibleItemIndex > headerItemCount ||
                            episodeListState.firstVisibleItemScrollOffset > 220,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 14.dp + navigationPadding),
                    ) {
                        PlayerGlassIconButton(
                            icon = Icons.Default.KeyboardArrowUp,
                            label = "Top",
                            onClick = {
                                coroutineScope.launch {
                                    episodeListState.animateScrollToItem(0)
                                }
                            },
                            emphasized = true,
                            size = 48.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EtsumePlayerTitleBar(
    animeTitle: String,
    mediaTitle: String,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = etsumeGlassContainerColor(0.64f),
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayerGlassIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                label = androidStringResource(R.string.etsume_action_back),
                onClick = onBackPress,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = animeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = mediaTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PlayerTransportCluster(
    paused: Boolean,
    hasPrevious: Boolean,
    hasNext: Boolean,
    showLoadingIndicator: Boolean = false,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerGlassIconButton(
            icon = Icons.Default.SkipPrevious,
            label = "Previous",
            enabled = hasPrevious,
            onClick = onPrevious,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(contentAlignment = Alignment.Center) {
            if (showLoadingIndicator) {
                CircularProgressIndicator(
                    modifier = Modifier.size(66.dp),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = Color.White.copy(alpha = 0.15f),
                )
            }
            PlayerGlassIconButton(
                icon = if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                label = if (paused) "Play" else "Pause",
                onClick = onPlayPause,
                emphasized = true,
                size = 52.dp,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        PlayerGlassIconButton(
            icon = Icons.Default.SkipNext,
            label = "Next",
            enabled = hasNext,
            onClick = onNext,
        )
    }
}

@Composable
private fun PlayerGlassIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    tinted: Boolean = false,
    enabled: Boolean = true,
    size: androidx.compose.ui.unit.Dp = 44.dp,
) {
    val contentColor = if (emphasized || tinted) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        shape = CircleShape,
        color = if (emphasized || tinted) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else etsumeGlassContainerColor(0.6f),
        border = BorderStroke(
            1.dp,
            if (emphasized || tinted) MaterialTheme.colorScheme.primary.copy(alpha = 0.34f) else etsumeGlassBorderColor(0.24f),
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (emphasized) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(etsumeAccentBrush(alpha = 0.95f)),
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) contentColor else contentColor.copy(alpha = 0.4f),
            )
        }
    }
}

@Composable
internal fun PlayerActionChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (emphasized) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else etsumeGlassContainerColor(0.58f),
        border = BorderStroke(
            1.dp,
            if (emphasized) MaterialTheme.colorScheme.primary.copy(alpha = 0.34f) else etsumeGlassBorderColor(0.24f),
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun CollapsiblePlayerSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    toggleLabel: String? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.extraLarge,
        color = etsumeGlassContainerColor(0.54f),
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.24f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = toggleLabel ?: if (expanded) {
                        androidStringResource(R.string.etsume_action_show_less)
                    } else {
                        androidStringResource(R.string.etsume_action_show_more)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onToggle),
                )
            }
            AnimatedVisibility(visible = expanded) {
                content()
            }
        }
    }
}

@Composable
private fun LandscapeTimelineStrip(
    position: Float,
    duration: Float,
    readAhead: Float,
    chapters: kotlinx.collections.immutable.ImmutableList<Segment>,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${Utils.prettyTime(position.toInt())} / ${Utils.prettyTime(duration.toInt())}",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier.widthIn(min = 96.dp),
        )
        Seeker(
            value = position.coerceIn(0f, duration),
            range = 0f..duration,
            onValueChange = onSeek,
            onValueChangeFinished = {},
            readAheadValue = readAhead,
            segments = chapters,
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            colors = SeekerDefaults.seekerColors(
                progressColor = MaterialTheme.colorScheme.secondary,
                thumbColor = MaterialTheme.colorScheme.secondary,
                trackColor = Color.White.copy(alpha = 0.22f),
                readAheadColor = Color.White.copy(alpha = 0.12f),
            ),
        )
    }
}

@Composable
private fun PortraitTimelineStrip(
    position: Float,
    duration: Float,
    readAhead: Float,
    chapters: kotlinx.collections.immutable.ImmutableList<Segment>,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = "${Utils.prettyTime(position.toInt())} / ${Utils.prettyTime(duration.toInt())}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.92f),
        )
        Seeker(
            value = position.coerceIn(0f, duration),
            range = 0f..duration,
            onValueChange = onSeek,
            onValueChangeFinished = {},
            readAheadValue = readAhead,
            segments = chapters,
            modifier = Modifier.fillMaxWidth(),
            colors = SeekerDefaults.seekerColors(
                progressColor = MaterialTheme.colorScheme.secondary,
                thumbColor = MaterialTheme.colorScheme.secondary,
                trackColor = Color.White.copy(alpha = 0.22f),
                readAheadColor = Color.White.copy(alpha = 0.12f),
            ),
        )
    }
}

@Composable
private fun PortraitEpisodeCard(
    episode: Episode,
    fallbackThumbnailUrl: String?,
    isCurrent: Boolean,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val episodeNumber = formatEpisodeNumber(episode.episode_number)
    val thumbnailUrl = episode.preview_url?.takeIf { it.isNotBlank() } ?: fallbackThumbnailUrl?.takeIf { it.isNotBlank() }
    val watchedFraction = when {
        episode.seen -> 1f
        episode.total_seconds > 0L && episode.last_second_seen > 0L -> {
            (episode.last_second_seen.toFloat() / episode.total_seconds.toFloat()).coerceIn(0f, 1f)
        }
        else -> 0f
    }
    val stateLabel = when {
        isCurrent -> androidStringResource(R.string.etsume_reader_current_badge)
        episode.seen -> androidStringResource(R.string.etsume_reader_completed)
        episode.last_second_seen > 0L -> androidStringResource(R.string.etsume_action_continue)
        else -> androidStringResource(R.string.etsume_action_open)
    }
    val metaLabel = buildString {
        append(stateLabel)
    }
    val subtitle = when {
        episode.last_second_seen > 0L -> androidStringResource(
            R.string.etsume_player_resume_from,
            Utils.prettyTime((episode.last_second_seen / 1000L).toInt()),
        )
        episode.seen -> androidStringResource(R.string.etsume_reader_completed)
        else -> androidStringResource(R.string.etsume_player_ready_to_open)
    }
    val actionLabel = when {
        isCurrent || episode.last_second_seen > 0L -> androidStringResource(R.string.etsume_action_continue)
        else -> androidStringResource(R.string.etsume_action_open)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else etsumeGlassContainerColor(0.48f),
        border = BorderStroke(
            1.dp,
            if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.30f) else etsumeGlassBorderColor(0.24f),
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = episode.name,
                        modifier = Modifier
                            .size(width = 64.dp, height = 36.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.size(width = 64.dp, height = 36.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = androidStringResource(R.string.etsume_player_episode_label, episodeNumber),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    Text(
                        text = metaLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = episode.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${androidStringResource(R.string.etsume_player_episode_label, episodeNumber)} | $subtitle",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                PlayerActionChip(
                    icon = Icons.Default.PlayArrow,
                    label = actionLabel,
                    emphasized = isCurrent,
                    onClick = onOpen,
                    modifier = Modifier.widthIn(min = 82.dp),
                )
            }

            if (watchedFraction > 0f) {
                LinearProgressIndicator(
                    progress = { watchedFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 72.dp, end = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun rememberSourceUrl(
    anime: Anime?,
    source: AnimeSource?,
): String? {
    return remember(anime?.id, anime?.url, source) {
        val rawUrl = anime?.url?.trim().orEmpty()
        when {
            rawUrl.isBlank() -> null
            rawUrl.startsWith("http://") || rawUrl.startsWith("https://") -> rawUrl
            source is AnimeHttpSource -> "${source.baseUrl.trimEnd('/')}/${rawUrl.trimStart('/')}"
            else -> null
        }
    }
}

private fun openExternalUrl(
    context: Context,
    url: String,
) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }.onFailure {
        context.toast(it.message ?: url)
    }
}

private fun formatEpisodeNumber(number: Float): String {
    return if (number == number.roundToInt().toFloat()) {
        number.roundToInt().toString()
    } else {
        number.toString()
    }
}

private fun sameEpisode(
    episode: Episode,
    currentEpisode: Episode?,
): Boolean {
    currentEpisode ?: return false
    return when {
        episode.id != null && currentEpisode.id != null -> episode.id == currentEpisode.id
        episode.url.isNotBlank() && currentEpisode.url.isNotBlank() -> episode.url == currentEpisode.url
        else -> episode.episode_number == currentEpisode.episode_number && episode.name == currentEpisode.name
    }
}

