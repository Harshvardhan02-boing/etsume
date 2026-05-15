package eu.kanade.presentation.more.onboarding

import android.content.ActivityNotFoundException
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.presentation.more.settings.screen.SettingsDataScreen
import eu.kanade.tachiyomi.util.system.isTvBox
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.flow.collectLatest
import tachiyomi.core.common.storage.AndroidStorageFolderProvider
import tachiyomi.domain.storage.service.StoragePreferences
import tachiyomi.i18n.MR
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

internal class StorageStep : OnboardingStep {

    private val storagePref = Injekt.get<StoragePreferences>().baseStorageDirectory()
    private val folderProvider = Injekt.get<AndroidStorageFolderProvider>()

    private var _isComplete by mutableStateOf(false)

    override val isComplete: Boolean
        get() = _isComplete

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val handler = LocalUriHandler.current

        val isTvBox = isTvBox(LocalContext.current)

        val pickStorageLocation = SettingsDataScreen.storageLocationPicker(storagePref)

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                color = etsumeGlassContainerColor(0.40f),
                border = BorderStroke(1.dp, etsumeGlassBorderColor(0.22f)),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Select a folder where Etsume will store chapter downloads, backups, and more.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Selected folder: ${SettingsDataScreen.storageLocationText(storagePref)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
            }

            if (isTvBox) {
                if (!storagePref.isSet()) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val storage = folderProvider.directory()
                            if (!storage.exists()) {
                                storage.mkdirs()
                            }
                            storagePref.set(storagePref.get())
                        },
                    ) {
                        Text(stringResource(AYMR.strings.onboarding_storage_action_create_folder))
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            try {
                                pickStorageLocation.launch(null)
                            } catch (e: ActivityNotFoundException) {
                                context.toast(MR.strings.file_picker_error)
                            }
                        },
                    ) {
                        Text(stringResource(MR.strings.onboarding_storage_action_select))
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { handler.openUri(SettingsDataScreen.HELP_URL) },
                    ) {
                        Text(stringResource(MR.strings.onboarding_storage_help_action))
                    }
                }
            }

            if (isTvBox && storagePref.isSet()) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { handler.openUri(SettingsDataScreen.HELP_URL) },
                ) {
                    Text(stringResource(MR.strings.onboarding_storage_help_action))
                }
            }

            Text(
                text = stringResource(MR.strings.onboarding_storage_help_info, stringResource(MR.strings.app_name)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4,
            )
        }

        LaunchedEffect(Unit) {
            storagePref.changes()
                .collectLatest { _isComplete = storagePref.isSet() }
        }
    }
}
