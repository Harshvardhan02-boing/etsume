package eu.kanade.tachiyomi.ui.player.controls.components.sheets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.presentation.comments.EtsumeCommentPlaceholder
import eu.kanade.presentation.player.components.PlayerSheet
import tachiyomi.presentation.core.components.material.padding

@Composable
fun CommentsSheet(
    draftKey: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlayerSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        EtsumeCommentPlaceholder(
            draftKey = draftKey,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.padding.medium)
                .verticalScroll(rememberScrollState()),
        )
    }
}
