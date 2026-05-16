package eu.kanade.presentation.more.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.theme.EtsumeAuroraBackdrop
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun PreferenceScaffold(
    titleRes: StringResource,
    subtitleRes: StringResource? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressed: (() -> Unit)? = null,
    itemsProvider: @Composable () -> List<Preference>,
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            AppBar(
                title = stringResource(titleRes),
                subtitle = subtitleRes?.let { stringResource(it) },
                navigateUp = onBackPressed,
                actions = actions,
                scrollBehavior = it,
            )
        },
        content = { contentPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                EtsumeAuroraBackdrop()
                PreferenceScreen(
                    items = itemsProvider(),
                    contentPadding = contentPadding,
                )
            }
        },
    )
}
