package com.example.medimate.admin.usersManagement.userDocumentation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medimate.admin.usersManagement.usersView.UserDocumentationViewModel
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.PurpleLight2
import com.example.medimate.ui.theme.PurpleMain
import com.example.medimate.ui.theme.White


@Composable
fun UserDocumentation(
    navController: NavController,
    userId: String?,
    viewModel: UserDocumentationViewModel = viewModel()
) {
    val isUploading by viewModel.isUploading.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (userId != null) {
        LaunchedEffect(userId) {
            userId.let { viewModel.loadUserData(it) }
        }
        val user by viewModel.user.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                if (uri != null) {
                    viewModel.uploadUserDocument(userId, uri, context)
                }
            }
        )
Surface( color = MaterialTheme.colorScheme.background,
    modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            user?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PurpleMain),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "User documentation",
                                    style = MaterialTheme.typography.headlineMedium, color = White
                                )
                                Spacer(modifier = Modifier.padding(16.dp))
                                Text("Name: ${user.name} ${user.surname}", color = White)
                                Text("Email: ${user.email}", color = White)
                                Text("Phone: ${user.phoneNumber}", color = White)
                                Text("Address: ${user.address}", color = White)
                            }
                        }
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PurpleLight2),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Uploaded files:", style = MaterialTheme.typography.titleMedium,
                                color = PurpleMain
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (user.documents.isEmpty()) {
                                Text("No files uploaded", color = PurpleMain)
                            } else {
                                user.documents.forEach { url ->
                                    val filename = url.substringAfterLast("/")
                                    Text(
                                        text = filename,
                                        color = PurpleMain,
                                        modifier = Modifier
                                            .clickable {
                                                val intent = Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(url)
                                                )
                                                context.startActivity(intent)
                                            }
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                        MediMateButton(
                            text = if (isUploading) "Uploading..." else "Upload File",
                            onClick = { if (!isUploading) filePickerLauncher.launch("*/*") },
                            enabled = !isUploading,
                            loading = isUploading)
                        Spacer(modifier = Modifier.weight(1f))
                        MediMateButton("Back",  modifier = Modifier.fillMaxWidth(),onClick = { navController.popBackStack() })
                }
            } ?: run {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("User not found")
                }
            }
        }
}
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No user ID provided")
        }
    }
}