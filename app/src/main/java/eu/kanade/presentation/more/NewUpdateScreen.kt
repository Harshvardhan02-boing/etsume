package eu.kanade.presentation.more

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.string.RichTextStringStyle
import eu.kanade.presentation.components.EtsumeNoticeScreen
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun NewUpdateScreen(
    versionName: String,
    changelogInfo: String,
    onOpenInBrowser: () -> Unit,
    onRejectUpdate: () -> Unit,
    onAcceptUpdate: () -> Unit,
) {
    EtsumeNoticeScreen(
        icon = Icons.Outlined.NewReleases,
        headingText = androidStringResource(eu.kanade.tachiyomi.R.string.etsume_update_shell_title),
        subtitleText = androidStringResource(eu.kanade.tachiyomi.R.string.etsume_update_shell_subtitle),
        acceptText = stringResource(MR.strings.update_check_confirm),
        rejectText = stringResource(MR.strings.action_not_now),
        onAcceptClick = onAcceptUpdate,
        onRejectClick = onRejectUpdate,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = etsumeGlassContainerColor(0.62f),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            RichText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.large),
                style = RichTextStyle(
                    stringStyle = RichTextStringStyle(
                        linkStyle = SpanStyle(color = MaterialTheme.colorScheme.primary),
                    ),
                ),
            ) {
                Text(
                    text = versionName,
                    modifier = Modifier.padding(bottom = MaterialTheme.padding.small),
                    style = MaterialTheme.typography.titleMedium,
                )
                Markdown(content = changelogInfo)

                TextButton(
                    onClick = onOpenInBrowser,
                    modifier = Modifier.padding(top = MaterialTheme.padding.small),
                ) {
                    Text(text = stringResource(MR.strings.update_check_open))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.padding(start = MaterialTheme.padding.extraSmall),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun NewUpdateScreenPreview() {
    TachiyomiPreviewTheme {
        NewUpdateScreen(
            versionName = "v0.99.9",
            changelogInfo = """
                ## Yay
                Foobar

                ### More info
                - Hello
                - World
            """.trimIndent(),
            onOpenInBrowser = {},
            onRejectUpdate = {},
            onAcceptUpdate = {},
        )
    }
}
