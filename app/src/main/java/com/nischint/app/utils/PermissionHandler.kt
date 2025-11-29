package com.nischint.app.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Permission state for audio recording
 */
sealed class AudioPermissionState {
    object Granted : AudioPermissionState()
    object Denied : AudioPermissionState()
    object PermanentlyDenied : AudioPermissionState()
    object NotRequested : AudioPermissionState()
}

/**
 * Check if RECORD_AUDIO permission is granted
 */
fun Context.hasAudioPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Check if permission was permanently denied
 */
fun Activity.isAudioPermissionPermanentlyDenied(): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        Manifest.permission.RECORD_AUDIO
    ) && !hasAudioPermission()
}

/**
 * Composable function to handle audio permission requests
 * 
 * Usage:
 * ```
 * val (permissionState, requestPermission) = rememberAudioPermission()
 * 
 * when (permissionState) {
 *     is AudioPermissionState.Granted -> { /* Use microphone */ }
 *     is AudioPermissionState.Denied -> { /* Show rationale */ }
 *     is AudioPermissionState.PermanentlyDenied -> { /* Redirect to settings */ }
 *     is AudioPermissionState.NotRequested -> { /* Initial state */ }
 * }
 * 
 * Button(onClick = { requestPermission() }) { Text("Request Permission") }
 * ```
 */
@Composable
fun rememberAudioPermission(): Pair<AudioPermissionState, () -> Unit> {
    val context = LocalContext.current
    var permissionState by remember {
        mutableStateOf<AudioPermissionState>(
            if (context.hasAudioPermission()) {
                AudioPermissionState.Granted
            } else {
                AudioPermissionState.NotRequested
            }
        )
    }
    
    // Permission request launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionState = if (isGranted) {
                AudioPermissionState.Granted
            } else {
                // Check if permanently denied
                val activity = context as? Activity
                if (activity?.isAudioPermissionPermanentlyDenied() == true) {
                    AudioPermissionState.PermanentlyDenied
                } else {
                    AudioPermissionState.Denied
                }
            }
        }
    )
    
    // Function to request permission
    val requestPermission: () -> Unit = {
        if (context.hasAudioPermission()) {
            permissionState = AudioPermissionState.Granted
        } else {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    
    return Pair(permissionState, requestPermission)
}

/**
 * Composable dialog to explain why audio permission is needed
 */
@Composable
fun AudioPermissionRationaleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.nischint.app.ui.theme.SurfaceDark.copy(alpha = 0.95f),
        modifier = Modifier.graphicsLayer {
            // Blur effect for dialog
            alpha = 0.95f
            shadowElevation = 16f
        },
        title = {
            Text(
                text = "🎤 Microphone Permission",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "Nischint ko voice commands ke liye microphone access chahiye. " +
                        "Aap bol kar navigate kar sakte ho: 'Bike', 'Tracker', 'Income' etc.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = com.nischint.app.ui.theme.YellowPrimary
                )
            ) {
                Text("Allow")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = com.nischint.app.ui.theme.TextGray
                )
            }
        }
    )
}

/**
 * Composable dialog for permanently denied permission
 * Guides user to app settings
 */
@Composable
fun AudioPermissionDeniedDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.nischint.app.ui.theme.SurfaceDark.copy(alpha = 0.95f),
        modifier = Modifier.graphicsLayer {
            // Blur effect for dialog
            alpha = 0.95f
            shadowElevation = 16f
        },
        title = {
            Text(
                text = "⚠️ Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "Microphone permission permanently denied hai. Voice commands use karne ke liye " +
                        "app settings mein jaake permission enable karo.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = com.nischint.app.ui.theme.YellowPrimary
                )
            ) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = com.nischint.app.ui.theme.TextGray
                )
            }
        }
    )
}

/**
 * Helper function to open app settings
 */
fun Context.openAppSettings() {
    val intent = android.content.Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        android.net.Uri.fromParts("package", packageName, null)
    )
    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

