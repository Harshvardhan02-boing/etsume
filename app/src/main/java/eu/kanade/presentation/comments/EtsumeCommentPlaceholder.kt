package eu.kanade.presentation.comments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.kanade.domain.comments.service.EtsumeCommentPreferences
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.util.system.toast
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun EtsumeCommentPlaceholder(
    draftKey: String,
    modifier: Modifier = Modifier,
    title: String = androidStringResource(R.string.etsume_comments_title),
    showTitle: Boolean = true,
    compact: Boolean = false,
) {
    val context = LocalContext.current
    val preferences = remember {
        runCatching { Injekt.get<EtsumeCommentPreferences>() }.getOrNull()
    }

    var draft by rememberSaveable(draftKey) {
        mutableStateOf(preferences?.commentDraft(draftKey)?.get().orEmpty())
    }
    var spoiler by rememberSaveable(draftKey) {
        mutableStateOf(preferences?.commentSpoiler(draftKey)?.get() ?: false)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = etsumeGlassContainerColor(alpha = 0.58f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, etsumeGlassBorderColor(alpha = 0.28f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (showTitle) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = androidStringResource(R.string.etsume_comment_local_only),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = if (compact) 2 else 3,
                maxLines = if (compact) 4 else 6,
                placeholder = {
                    Text(text = androidStringResource(R.string.etsume_comment_hint))
                },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilterChip(
                    selected = spoiler,
                    onClick = { spoiler = !spoiler },
                    label = { Text(text = androidStringResource(R.string.etsume_comment_spoiler)) },
                )
                Button(
                    onClick = {
                        preferences?.commentDraft(draftKey)?.set(draft.trim())
                        preferences?.commentSpoiler(draftKey)?.set(spoiler)
                        context.toast(context.getString(R.string.etsume_comment_saved_local))
                    },
                    enabled = draft.isNotBlank() || spoiler,
                ) {
                    Text(text = androidStringResource(R.string.etsume_comment_save_draft))
                }
            }
            Text(
                text = androidStringResource(R.string.etsume_comment_coming_soon),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CommentActionPill(label = androidStringResource(R.string.etsume_comment_reply))
                CommentActionPill(label = androidStringResource(R.string.etsume_comment_like))
                CommentActionPill(label = androidStringResource(R.string.etsume_comment_dislike))
            }
        }
    }
}

@Composable
private fun CommentActionPill(
    label: String,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.large,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
