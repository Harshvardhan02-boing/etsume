package eu.kanade.presentation.reader.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.presentation.comments.EtsumeCommentPlaceholder

@Composable
internal fun ColumnScope.CommentsPage(
    draftKey: String,
) {
    EtsumeCommentPlaceholder(
        draftKey = draftKey,
        modifier = Modifier.fillMaxWidth(),
    )
}
