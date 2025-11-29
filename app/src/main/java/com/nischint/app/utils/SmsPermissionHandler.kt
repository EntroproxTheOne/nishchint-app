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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Permission state for SMS reading
 */
sealed class SmsPermissionState {
    object Granted : SmsPermissionState()
    object Denied : SmsPermissionState()
    object PermanentlyDenied : SmsPermissionState()
    object NotRequested : SmsPermissionState()
}

/**
 * Check if READ_SMS permission is granted
 */
fun Context.hasSmsPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_SMS
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Check if permission was permanently denied
 */
fun Activity.isSmsPermissionPermanentlyDenied(): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        Manifest.permission.READ_SMS
    ) && !hasSmsPermission()
}

/**
 * Composable function to handle SMS permission requests
 */
@Composable
fun rememberSmsPermission(): Pair<SmsPermissionState, () -> Unit> {
    val context = LocalContext.current
    var permissionState by remember {
        mutableStateOf<SmsPermissionState>(
            if (context.hasSmsPermission()) {
                SmsPermissionState.Granted
            } else {
                SmsPermissionState.NotRequested
            }
        )
    }
    
    // Permission request launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionState = if (isGranted) {
                SmsPermissionState.Granted
            } else {
                // Check if permanently denied
                val activity = context as? Activity
                if (activity?.isSmsPermissionPermanentlyDenied() == true) {
                    SmsPermissionState.PermanentlyDenied
                } else {
                    SmsPermissionState.Denied
                }
            }
        }
    )
    
    // Function to request permission
    val requestPermission: () -> Unit = {
        if (context.hasSmsPermission()) {
            permissionState = SmsPermissionState.Granted
        } else {
            launcher.launch(Manifest.permission.READ_SMS)
        }
    }
    
    return Pair(permissionState, requestPermission)
}

/**
 * Composable dialog to explain why SMS permission is needed
 */
@Composable
fun SmsPermissionRationaleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.nischint.app.ui.theme.SurfaceDark.copy(alpha = 0.95f),
        modifier = androidx.compose.ui.Modifier.graphicsLayer {
            alpha = 0.95f
            shadowElevation = 16f
        },
        title = {
            androidx.compose.material3.Text(
                text = "📱 SMS Permission",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                color = com.nischint.app.ui.theme.TextWhite
            )
        },
        text = {
            androidx.compose.material3.Text(
                text = "Nischint ko transactions detect karne ke liye SMS read karne ki permission chahiye. " +
                        "Aapke bank aur payment apps ke SMS se automatically transactions add ho jayenge.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = com.nischint.app.ui.theme.TextGray
            )
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = com.nischint.app.ui.theme.YellowPrimary
                )
            ) {
                androidx.compose.material3.Text("Allow")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text(
                    "Cancel",
                    color = com.nischint.app.ui.theme.TextGray
                )
            }
        }
    )
}

/**
 * Composable dialog for permanently denied SMS permission
 */
@Composable
fun SmsPermissionDeniedDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.nischint.app.ui.theme.SurfaceDark.copy(alpha = 0.95f),
        modifier = androidx.compose.ui.Modifier.graphicsLayer {
            alpha = 0.95f
            shadowElevation = 16f
        },
        title = {
            androidx.compose.material3.Text(
                text = "⚠️ Permission Required",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                color = com.nischint.app.ui.theme.TextWhite
            )
        },
        text = {
            androidx.compose.material3.Text(
                text = "SMS permission permanently denied hai. Transactions detect karne ke liye " +
                        "app settings mein jaake permission enable karo.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = com.nischint.app.ui.theme.TextGray
            )
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = onOpenSettings,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = com.nischint.app.ui.theme.YellowPrimary
                )
            ) {
                androidx.compose.material3.Text("Open Settings")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text(
                    "Cancel",
                    color = com.nischint.app.ui.theme.TextGray
                )
            }
        }
    )
}
