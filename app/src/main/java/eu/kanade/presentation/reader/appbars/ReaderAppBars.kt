package eu.kanade.presentation.reader.appbars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.EtsumeToolbarActionButton
import eu.kanade.presentation.components.UpIcon
import eu.kanade.tachiyomi.R
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

private val animationSpec = tween<IntOffset>(200)

@Composable
fun ReaderAppBars(
    visible: Boolean,
    fullscreen: Boolean,
    mangaTitle: String?,
    chapterTitle: String?,
    navigateUp: () -> Unit,
    onClickTopAppBar: () -> Unit,
    bookmarked: Boolean,
    onToggleBookmarked: () -> Unit,
    onOpenInWebView: (() -> Unit)?,
    onOpenInBrowser: (() -> Unit)?,
    onShare: (() -> Unit)?,
    onOpenChapterList: () -> Unit,
    currentPage: Int,
    totalPages: Int,
    onPageIndexChange: (Int) -> Unit,
    onClickComments: () -> Unit,
    onClickSettings: () -> Unit,
) {
    var showOverflow by remember { mutableStateOf(false) }

    val modifierWithInsetsPadding = if (fullscreen) {
        Modifier.systemBarsPadding()
    } else {
        Modifier
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = animationSpec,
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = animationSpec,
            ),
        ) {
            Row(
                modifier = modifierWithInsetsPadding
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EtsumeToolbarActionButton(
                        onClick = navigateUp,
                        contentDescription = stringResource(MR.strings.action_bar_up_description),
                    ) {
                        UpIcon()
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = mangaTitle.orEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = chapterTitle.orEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EtsumeToolbarActionButton(
                        onClick = onOpenChapterList,
                        contentDescription = stringResource(MR.strings.chapters),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_video_chapter_20dp),
                            contentDescription = null,
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        EtsumeToolbarActionButton(
                            onClick = { showOverflow = true },
                            contentDescription = stringResource(MR.strings.label_more),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = null,
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflow,
                            onDismissRequest = { showOverflow = false },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            if (bookmarked) {
                                                MR.strings.action_remove_bookmark
                                            } else {
                                                MR.strings.action_bookmark
                                            },
                                        ),
                                    )
                                },
                                onClick = {
                                    showOverflow = false
                                    onToggleBookmarked()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (bookmarked) {
                                            Icons.Outlined.Bookmark
                                        } else {
                                            Icons.Outlined.BookmarkBorder
                                        },
                                        contentDescription = null,
                                    )
                                },
                            )
                            onOpenInWebView?.let {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(MR.strings.action_open_in_web_view)) },
                                    onClick = {
                                        showOverflow = false
                                        it()
                                    },
                                )
                            }
                            onOpenInBrowser?.let {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(MR.strings.action_open_in_browser)) },
                                    onClick = {
                                        showOverflow = false
                                        it()
                                    },
                                )
                            }
                            onShare?.let {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(MR.strings.action_share)) },
                                    onClick = {
                                        showOverflow = false
                                        it()
                                    },
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(text = androidStringResource(R.string.etsume_action_open)) },
                                onClick = {
                                    showOverflow = false
                                    onClickTopAppBar()
                                },
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = animationSpec,
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = animationSpec,
            ),
        ) {
            Row(
                modifier = modifierWithInsetsPadding
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                BottomReaderBar(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPageIndexChange = onPageIndexChange,
                    onClickComments = onClickComments,
                    onClickSettings = onClickSettings,
                    modifier = Modifier.widthIn(max = 520.dp),
                )
            }
        }
    }
}
