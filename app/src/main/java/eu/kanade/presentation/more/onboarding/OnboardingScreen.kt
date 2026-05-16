package eu.kanade.presentation.more.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource as androidStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.presentation.theme.EtsumeAuroraBackdrop
import eu.kanade.presentation.theme.etsumeAccentBrush
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.tachiyomi.R
import soup.compose.material.motion.animation.materialSharedAxisX
import soup.compose.material.motion.animation.rememberSlideDistance
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onRestoreBackup: () -> Unit,
) {
    val slideDistance = rememberSlideDistance()
    var currentStep by rememberSaveable { mutableIntStateOf(0) }

    val themeStep = remember { ThemeStep() }
    val storageStep = remember { StorageStep() }
    val permissionStep = remember { PermissionStep() }
    val sourcePreferences = remember { Injekt.get<SourcePreferences>() }
    val showNsfwPref = remember { sourcePreferences.showNsfwSource() }
    val showNsfw by showNsfwPref.collectAsState()

    val lastStepIndex = 2
    val canContinue = when (currentStep) {
        2 -> storageStep.isComplete
        else -> true
    }

    BackHandler(enabled = currentStep != 0, onBack = { currentStep-- })

    Box(modifier = Modifier.fillMaxSize()) {
        EtsumeAuroraBackdrop()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OnboardingHero(
                step = currentStep,
                totalSteps = lastStepIndex + 1,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        materialSharedAxisX(
                            forward = targetState > initialState,
                            slideDistance = slideDistance,
                        )
                    },
                    label = "etsume_onboarding_page",
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 2.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        when (step) {
                            0 -> WelcomePage(
                                onStartFresh = { currentStep++ },
                                onRestoreBackup = onRestoreBackup,
                            )
                            1 -> PreferencesPage(
                                themeStep = themeStep,
                                showNsfw = showNsfw,
                                onShowNsfwChange = showNsfwPref::set,
                            )
                            else -> SetupPage(
                                storageStep = storageStep,
                                permissionStep = permissionStep,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (currentStep > 0) {
                    EtsumeGhostButton(
                        text = androidStringResource(R.string.etsume_action_back),
                        modifier = Modifier.weight(1f),
                        onClick = { currentStep-- },
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                EtsumePrimaryButton(
                    text = if (currentStep == lastStepIndex) {
                        androidStringResource(R.string.etsume_onboarding_enter)
                    } else {
                        androidStringResource(R.string.etsume_action_continue)
                    },
                    enabled = canContinue,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (currentStep == lastStepIndex) {
                            onComplete()
                        } else {
                            currentStep++
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun OnboardingHero(
    step: Int,
    totalSteps: Int,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            modifier = Modifier.size(88.dp),
            shape = CircleShape,
            color = Color(0xFF0A0F1C),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, etsumeGlassBorderColor(0.26f)),
        ) {
            Image(
                painter = painterResource(R.drawable.app_launcher),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }

        Text(
            text = androidStringResource(R.string.etsume_onboarding_heading),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = androidStringResource(R.string.etsume_onboarding_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == step) {
                                etsumeAccentBrush(0.96f)
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                                    ),
                                )
                            },
                        ),
                )
            }
        }
    }
}

@Composable
private fun WelcomePage(
    onStartFresh: () -> Unit,
    onRestoreBackup: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    PageHeader(
        title = androidStringResource(R.string.etsume_onboarding_welcome_title),
        subtitle = androidStringResource(R.string.etsume_onboarding_welcome_subtitle),
    )

    OnboardingSectionCard {
        Text(
            text = androidStringResource(R.string.etsume_onboarding_welcome_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(14.dp))
        EtsumePrimaryButton(
            text = androidStringResource(R.string.etsume_onboarding_start_fresh),
            onClick = onStartFresh,
        )
    }

    OnboardingSectionCard {
        Text(
            text = androidStringResource(R.string.etsume_onboarding_returning_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = androidStringResource(R.string.etsume_onboarding_returning_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(14.dp))
        EtsumeGhostButton(
            text = stringResource(MR.strings.pref_restore_backup),
            onClick = onRestoreBackup,
        )
    }

    EtsumeGhostButton(
        text = stringResource(MR.strings.getting_started_guide),
        onClick = { uriHandler.openUri(GETTING_STARTED_URL) },
    )
}

@Composable
private fun PreferencesPage(
    themeStep: ThemeStep,
    showNsfw: Boolean,
    onShowNsfwChange: (Boolean) -> Unit,
) {
    PageHeader(
        title = androidStringResource(R.string.etsume_onboarding_theme_title),
        subtitle = androidStringResource(R.string.etsume_onboarding_theme_subtitle),
    )

    themeStep.Content()

    OnboardingSectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = androidStringResource(R.string.etsume_action_show_nsfw),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = androidStringResource(R.string.etsume_onboarding_nsfw_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = showNsfw,
                onCheckedChange = onShowNsfwChange,
            )
        }
    }
}

@Composable
private fun SetupPage(
    storageStep: StorageStep,
    permissionStep: PermissionStep,
) {
    PageHeader(
        title = androidStringResource(R.string.etsume_onboarding_setup_title),
        subtitle = androidStringResource(R.string.etsume_onboarding_setup_subtitle),
    )

    OnboardingSectionCard(
        title = androidStringResource(R.string.etsume_onboarding_section_storage),
    ) {
        storageStep.Content()
    }

    OnboardingSectionCard(
        title = androidStringResource(R.string.etsume_onboarding_section_permissions),
    ) {
        permissionStep.Content()
    }
}

@Composable
private fun PageHeader(
    title: String,
    subtitle: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun OnboardingSectionCard(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = etsumeGlassContainerColor(0.48f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.22f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                content()
            }
        }
    }
}

@Composable
private fun EtsumePrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    if (enabled) etsumeAccentBrush(0.96f) else Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                    ),
                )
                .padding(horizontal = 22.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

@Composable
private fun EtsumeGhostButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        color = etsumeGlassContainerColor(0.44f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, etsumeGlassBorderColor(0.2f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
