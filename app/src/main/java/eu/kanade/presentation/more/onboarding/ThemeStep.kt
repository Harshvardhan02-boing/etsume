package eu.kanade.presentation.more.onboarding

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.kanade.domain.ui.model.AppTheme
import eu.kanade.domain.ui.model.ThemeMode
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.domain.ui.model.setAppCompatDelegateThemeMode
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import tachiyomi.presentation.core.util.collectAsState
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

internal class ThemeStep : OnboardingStep {

    override val isComplete: Boolean = true

    private val uiPreferences: UiPreferences = Injekt.get()

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val themeModePref = uiPreferences.themeMode()
        val themeMode by themeModePref.collectAsState()

        val appThemePref = uiPreferences.appTheme()
        val appTheme by appThemePref.collectAsState()

        val amoledPref = uiPreferences.themeDarkAmoled()
        val amoled by amoledPref.collectAsState()

        Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        ) {
            ThemeSection(
                title = "Appearance",
                subtitle = "Choose how Etsume should behave on this device.",
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
                ) {
                    ThemeMode.entries.forEach { option ->
                        OutlinedButton(
                            modifier = Modifier.widthIn(min = 88.dp),
                            onClick = {
                                themeModePref.set(option)
                                setAppCompatDelegateThemeMode(option)
                                (context as? Activity)?.recreate()
                            },
                        ) {
                            Text(
                                text = stringResource(option.toTitleRes()),
                                color = if (themeMode == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            ThemeSection(
                title = "Accents",
                subtitle = "Theme selection. More options stay available in settings.",
            ) {
                LazyRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
                ) {
                    items(
                        AppTheme.entries.filter { it.titleRes != null },
                        key = { it.name },
                    ) { option ->
                        ChoiceChip(
                            label = stringResource(option.titleRes!!),
                            selected = appTheme == option,
                            onClick = {
                                appThemePref.set(option)
                                (context as? Activity)?.recreate()
                            },
                        )
                    }
                }
            }

            if (themeMode != ThemeMode.LIGHT) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = etsumeGlassContainerColor(0.42f),
                    border = BorderStroke(1.dp, etsumeGlassBorderColor(0.22f)),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 240.dp)
                                .padding(end = 12.dp),
                        ) {
                            Text(
                                text = "Deep blacks",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Use amoled-style dark panels where available.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = amoled,
                            onCheckedChange = amoledPref::set,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ThemeSection(
        title: String,
        subtitle: String,
        content: @Composable () -> Unit,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = etsumeGlassContainerColor(0.42f),
            border = BorderStroke(1.dp, etsumeGlassBorderColor(0.22f)),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
                ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                content()
            }
        }
    }

    @Composable
    private fun ChoiceChip(
        label: String,
        selected: Boolean,
        onClick: () -> Unit,
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else etsumeGlassContainerColor(0.40f),
            border = BorderStroke(
                1.dp,
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.32f) else etsumeGlassBorderColor(0.22f),
            ),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    private fun ThemeMode.toTitleRes() = when (this) {
        ThemeMode.SYSTEM -> tachiyomi.i18n.MR.strings.theme_system
        ThemeMode.LIGHT -> tachiyomi.i18n.MR.strings.theme_light
        ThemeMode.DARK -> tachiyomi.i18n.MR.strings.theme_dark
    }
}
