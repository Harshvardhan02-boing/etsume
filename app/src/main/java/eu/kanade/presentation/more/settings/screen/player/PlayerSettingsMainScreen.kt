package eu.kanade.presentation.more.settings.screen.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource as androidStringResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.SettingsSearchScreen
import eu.kanade.presentation.more.settings.screen.SettingsSectionItem
import eu.kanade.presentation.more.settings.screen.SettingsSectionNavigatorScaffold
import eu.kanade.presentation.more.settings.screen.player.custombutton.PlayerSettingsCustomButtonScreen
import eu.kanade.presentation.more.settings.screen.player.editor.PlayerSettingsEditorScreen
import eu.kanade.presentation.util.LocalBackPress
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.R
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.presentation.core.i18n.stringResource
import cafe.adriel.voyager.core.screen.Screen as VoyagerScreen

class PlayerSettingsMainScreen(private val mainSettings: Boolean) : Screen() {
    @Composable
    override fun Content() {
        Content(twoPane = false)
    }

    @Composable
    fun Content(twoPane: Boolean) {
        val navigator = LocalNavigator.currentOrThrow
        val backPress = LocalBackPress.currentOrThrow
        SettingsSectionNavigatorScaffold(
            title = stringResource(
                if (mainSettings) {
                    AYMR.strings.label_player
                } else {
                    AYMR.strings.label_player_settings
                },
            ),
            subtitle = androidStringResource(R.string.etsume_player_settings_tagline),
            items = items,
            twoPane = twoPane,
            onBackPressed = backPress::invoke,
            onSearchClick = { navigator.navigate(SettingsSearchScreen(true), twoPane) },
            isSelected = { item -> item.screen::class == navigator.items.first()::class },
            onNavigate = { navigator.navigate(it, twoPane) },
        )
    }

    private fun Navigator.navigate(screen: VoyagerScreen, twoPane: Boolean) {
        if (twoPane) replaceAll(screen) else push(screen)
    }

    private val items = listOf(
        SettingsSectionItem(
            titleRes = AYMR.strings.pref_player_internal,
            subtitleRes = AYMR.strings.pref_player_internal_summary,
            icon = Icons.Outlined.PlayCircleOutline,
            screen = PlayerSettingsPlayerScreen,
        ),
        SettingsSectionItem(
            titleRes = AYMR.strings.pref_player_gestures,
            subtitleRes = AYMR.strings.pref_player_gestures_summary,
            icon = Icons.Outlined.Gesture,
            screen = PlayerSettingsGesturesScreen,
        ),
        SettingsSectionItem(
            titleRes = AYMR.strings.pref_player_decoder,
            subtitleRes = AYMR.strings.pref_player_decoder_summary,
            icon = Icons.Outlined.Memory,
            screen = PlayerSettingsDecoderScreen,
        ),
        SettingsSectionItem(
            titleRes = AYMR.strings.pref_player_subtitle,
            subtitleRes = AYMR.strings.pref_player_subtitle_summary,
            icon = Icons.Outlined.Subtitles,
            screen = PlayerSettingsSubtitleScreen,
        ),
        SettingsSectionItem(
            titleRes = AYMR.strings.pref_player_audio,
            subtitleRes = AYMR.strings.pref_player_audio_summary,
            icon = Icons.Outlined.Audiotrack,
            screen = PlayerSettingsAudioScreen,
        ),
        SettingsSectionItem(
            titleRes = AYMR.strings.pref_player_custom_button,
            subtitleRes = AYMR.strings.pref_player_custom_button_summary,
            icon = Icons.Outlined.Terminal,
            screen = PlayerSettingsCustomButtonScreen,
        ),
        SettingsSectionItem(
            titleRes = AYMR.strings.pref_player_editor,
            subtitleRes = AYMR.strings.pref_player_editor_summary,
            icon = Icons.Outlined.EditNote,
            screen = PlayerSettingsEditorScreen,
        ),
        SettingsSectionItem(
            titleRes = AYMR.strings.pref_player_advanced,
            subtitleRes = AYMR.strings.pref_player_advanced_summary,
            icon = Icons.Outlined.Code,
            screen = PlayerSettingsAdvancedScreen,
        ),
    )
}
