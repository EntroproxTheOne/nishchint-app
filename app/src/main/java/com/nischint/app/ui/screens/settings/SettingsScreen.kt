package com.nischint.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nischint.app.data.preferences.AppLanguage
import com.nischint.app.ui.components.NeomorphicCard
import com.nischint.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = TextWhite
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Settings Section
            SettingsSection(title = "User Settings") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Profile",
                    subtitle = "Edit your profile information",
                    onClick = onNavigateToProfile
                )
                
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = when (uiState.currentLanguage) {
                        com.nischint.app.data.preferences.AppLanguage.HINDI -> "हिंदी / Hindi"
                        com.nischint.app.data.preferences.AppLanguage.ENGLISH -> "English / अंग्रेजी"
                    },
                    onClick = { viewModel.showLanguageDialog() }
                )
                
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = if (uiState.notificationsEnabled) "Enabled" else "Disabled",
                    onClick = { viewModel.toggleNotifications() }
                )
            }
            
            // Database Management Section
            SettingsSection(title = "Database Management") {
                SettingsItem(
                    icon = Icons.Default.Folder,
                    title = "Database Size",
                    subtitle = uiState.databaseSize,
                    onClick = { /* Info only */ },
                    enabled = false
                )
                
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Clear All Data",
                    subtitle = "Delete all transactions and goals",
                    onClick = {
                        viewModel.showClearDataDialog = true
                    },
                    iconTint = RedDanger
                )
                
                SettingsItem(
                    icon = Icons.Default.FileDownload,
                    title = "Export Data",
                    subtitle = "Export transactions to CSV",
                    onClick = { viewModel.exportData() }
                )
                
                SettingsItem(
                    icon = Icons.Default.Restore,
                    title = "Reset App",
                    subtitle = "Reset to initial state",
                    onClick = {
                        viewModel.showResetDialog = true
                    },
                    iconTint = OrangeWarning
                )
            }
            
            // App Preferences Section
            SettingsSection(title = "App Preferences") {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = "Dark (Current)",
                    onClick = { /* TODO: Theme selector */ },
                    enabled = false
                )
                
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Privacy & Security",
                    subtitle = "Manage permissions and privacy",
                    onClick = { /* TODO: Privacy settings */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.CloudUpload,
                    title = "Backup & Sync",
                    subtitle = if (uiState.backupEnabled) "Enabled" else "Disabled",
                    onClick = { viewModel.toggleBackup() }
                )
            }
            
            // About Section
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "App Version",
                    subtitle = "1.0.0",
                    onClick = { /* Info only */ },
                    enabled = false
                )
                
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    subtitle = "Get help and contact support",
                    onClick = { /* TODO: Help screen */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Article,
                    title = "Privacy Policy",
                    subtitle = "View privacy policy",
                    onClick = { /* TODO: Privacy policy */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    subtitle = "View terms of service",
                    onClick = { /* TODO: Terms screen */ }
                )
            }
        }
    }
    
    // Clear Data Confirmation Dialog
    if (uiState.showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showClearDataDialog = false },
            containerColor = SurfaceDark.copy(alpha = 0.95f),
            modifier = Modifier.graphicsLayer {
                alpha = 0.95f
                shadowElevation = 16f
            },
            title = {
                Text(
                    "Clear All Data?",
                    color = TextWhite,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    "This will delete all transactions, goals, and user data. This action cannot be undone.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.clearAllData()
                            viewModel.showClearDataDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showClearDataDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
    
    // Reset App Confirmation Dialog
    if (uiState.showResetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showResetDialog = false },
            containerColor = SurfaceDark.copy(alpha = 0.95f),
            modifier = Modifier.graphicsLayer {
                alpha = 0.95f
                shadowElevation = 16f
            },
            title = {
                Text(
                    "Reset App?",
                    color = TextWhite,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    "This will reset the app to its initial state, including clearing all data and resetting onboarding.",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.resetApp()
                            viewModel.showResetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeWarning)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showResetDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
    
    // Language Selector Dialog
    if (uiState.showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLanguageDialog() },
            containerColor = SurfaceDark.copy(alpha = 0.95f),
            modifier = Modifier.graphicsLayer {
                alpha = 0.95f
                shadowElevation = 16f
            },
            title = {
                Text(
                    "Select Language",
                    color = TextWhite,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageOption(
                        language = AppLanguage.HINDI,
                        isSelected = uiState.currentLanguage == AppLanguage.HINDI,
                        onClick = { viewModel.setLanguage(AppLanguage.HINDI) }
                    )
                    LanguageOption(
                        language = AppLanguage.ENGLISH,
                        isSelected = uiState.currentLanguage == AppLanguage.ENGLISH,
                        onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.hideLanguageDialog() }) {
                    Text("Done", color = YellowPrimary)
                }
            }
        )
    }
}

@Composable
fun LanguageOption(
    language: AppLanguage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            YellowPrimary.copy(alpha = 0.2f)
        } else {
            SurfaceDark.copy(alpha = 0.5f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) YellowPrimary else TextWhite,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = YellowPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = YellowPrimary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        
        NeomorphicCard {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    iconTint: androidx.compose.ui.graphics.Color = YellowPrimary
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (enabled) {
            androidx.compose.ui.graphics.Color.Transparent
        } else {
            SurfaceDark.copy(alpha = 0.5f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) iconTint else TextGray,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) TextWhite else TextGray
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
            
            if (enabled) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

