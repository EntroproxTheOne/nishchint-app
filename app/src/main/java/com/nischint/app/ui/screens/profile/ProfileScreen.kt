package com.nischint.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nischint.app.data.database.AppDatabase
import com.nischint.app.ui.components.NeomorphicButton
import com.nischint.app.ui.components.NeomorphicCard
import com.nischint.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var nameText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf("") }
    var languageText by remember { mutableStateOf("hi") }
    
    // Update text fields when user loads
    LaunchedEffect(uiState.user) {
        uiState.user?.let { user ->
            nameText = user.name
            phoneText = user.phone
            languageText = user.language
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = TextWhite) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = YellowPrimary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.user?.name?.take(1)?.uppercase() ?: "U",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextOnYellow
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // User Info Card
            NeomorphicCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name Field
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text("Name", color = TextGray) },
                        enabled = uiState.isEditing,
                        readOnly = !uiState.isEditing,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedLabelColor = YellowPrimary,
                            unfocusedLabelColor = TextGray,
                            focusedBorderColor = YellowPrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )
                    
                    // Phone Field
                    OutlinedTextField(
                        value = phoneText,
                        onValueChange = { phoneText = it },
                        label = { Text("Phone", color = TextGray) },
                        enabled = uiState.isEditing,
                        readOnly = !uiState.isEditing,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedLabelColor = YellowPrimary,
                            unfocusedLabelColor = TextGray,
                            focusedBorderColor = YellowPrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )
                    
                    // Language Field
                    OutlinedTextField(
                        value = languageText,
                        onValueChange = { languageText = it },
                        label = { Text("Language", color = TextGray) },
                        enabled = uiState.isEditing,
                        readOnly = !uiState.isEditing,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedLabelColor = YellowPrimary,
                            unfocusedLabelColor = TextGray,
                            focusedBorderColor = YellowPrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Edit/Save Button
                    NeomorphicButton(
                        onClick = {
                            if (uiState.isEditing) {
                                viewModel.updateUser(nameText, phoneText, languageText)
                            } else {
                                viewModel.toggleEditMode()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (uiState.isEditing) Icons.Default.Save else Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isEditing) "Save Changes" else "Edit Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            // Loading indicator
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = YellowPrimary)
            }
            
            // Error message
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = RedDanger,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * ViewModel Factory for ProfileViewModel
 */
class ProfileViewModelFactory(
    private val context: android.content.Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(AppDatabase.getDatabase(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

