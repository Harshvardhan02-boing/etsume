package eu.kanade.presentation.more.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import eu.kanade.presentation.theme.etsumeGlassBorderColor
import eu.kanade.presentation.theme.etsumeGlassContainerColor
import eu.kanade.presentation.util.rememberRequestPackageInstallsPermissionState
import eu.kanade.tachiyomi.util.system.launchRequestPackageInstallsPermission
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

internal class PermissionStep : OnboardingStep {

    private var notificationGranted by mutableStateOf(false)
    private var batteryGranted by mutableStateOf(false)

    override val isComplete: Boolean = true

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        val installGranted = rememberRequestPackageInstallsPermissionState()

        DisposableEffect(lifecycleOwner.lifecycle) {
            val observer = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                            PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }
                    batteryGranted = context.getSystemService<PowerManager>()!!
                        .isIgnoringBatteryOptimizations(context.packageName)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
        ) {
            PermissionItem(
                title = stringResource(MR.strings.onboarding_permission_install_apps),
                subtitle = stringResource(MR.strings.onboarding_permission_install_apps_description),
                granted = installGranted,
                onButtonClick = {
                    context.launchRequestPackageInstallsPermission()
                },
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionRequester = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = {
                        // no-op. resulting checks is being done on resume
                    },
                )
                PermissionItem(
                    title = stringResource(MR.strings.onboarding_permission_notifications),
                    subtitle = stringResource(MR.strings.onboarding_permission_notifications_description),
                    granted = notificationGranted,
                    onButtonClick = { permissionRequester.launch(Manifest.permission.POST_NOTIFICATIONS) },
                )
            }

            PermissionItem(
                title = stringResource(MR.strings.onboarding_permission_ignore_battery_opts),
                subtitle = stringResource(MR.strings.onboarding_permission_ignore_battery_opts_description),
                granted = batteryGranted,
                onButtonClick = {
                    @SuppressLint("BatteryLife")
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                    context.startActivity(intent)
                },
            )
        }
    }

    @Composable
    private fun PermissionItem(
        title: String,
        subtitle: String,
        granted: Boolean,
        modifier: Modifier = Modifier,
        onButtonClick: () -> Unit,
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = etsumeGlassContainerColor(0.40f),
            border = BorderStroke(1.dp, etsumeGlassBorderColor(0.22f)),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Button(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .wrapContentWidth()
                            .widthIn(min = 102.dp),
                        onClick = {
                            if (!granted) {
                                onButtonClick()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                        Text(
                            if (granted) {
                                "Granted"
                            } else {
                                stringResource(MR.strings.onboarding_permission_action_grant)
                            },
                        )
                    }
                }
            }
        }
    }
}
