package eu.kanade.presentation.crash

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.EtsumeNoticeScreen
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.tachiyomi.util.CrashLogUtil
import kotlinx.coroutines.launch
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun CrashScreen(
    exception: Throwable?,
    onRestartClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    EtsumeNoticeScreen(
        icon = Icons.Outlined.BugReport,
        headingText = androidStringResource(eu.kanade.tachiyomi.R.string.etsume_crash_shell_title),
        subtitleText = androidStringResource(eu.kanade.tachiyomi.R.string.etsume_crash_shell_subtitle),
        acceptText = stringResource(MR.strings.crash_screen_restart_application),
        rejectText = stringResource(MR.strings.pref_dump_crash_logs),
        onAcceptClick = onRestartClick,
        onRejectClick = {
            scope.launch {
                CrashLogUtil(context).dumpLogs(exception)
            }
        },
    ) {
        Surface(
            modifier = Modifier
                .padding(vertical = MaterialTheme.padding.small)
                .padding(bottom = MaterialTheme.padding.small),
            shape = MaterialTheme.shapes.large,
            color = etsumeGlassContainerColor(0.62f),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Text(
                text = exception.toString(),
                modifier = Modifier
                    .padding(all = MaterialTheme.padding.small),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CrashScreenPreview() {
    TachiyomiPreviewTheme {
        CrashScreen(exception = RuntimeException("Dummy")) {}
    }
}
