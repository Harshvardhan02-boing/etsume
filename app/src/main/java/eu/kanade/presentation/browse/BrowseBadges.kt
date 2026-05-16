package eu.kanade.presentation.browse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tachiyomi.presentation.core.components.Badge

@Composable
fun InLibraryBadge(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (enabled) {
        Badge(
            imageVector = Icons.Outlined.CollectionsBookmark,
            modifier = modifier,
        )
    }
}
