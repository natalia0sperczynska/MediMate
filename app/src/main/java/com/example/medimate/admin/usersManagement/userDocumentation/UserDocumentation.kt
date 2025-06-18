package com.example.medimate.admin.usersManagement.userDocumentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medimate.admin.usersManagement.usersView.UserDocumentationViewModel


@Composable
fun UserDocumentation(navController: NavController, userId:String) {
    val viewModel: UserDocumentationViewModel = viewModel()

    LaunchedEffect(userId) {
        userId.let { viewModel.loadUserData(it) }
    }
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        user?.let { user ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text("User documentation", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.padding(16.dp))

                Text("Name: ${user.name} ${user.surname}")
                Text("Email: ${user.email}")
                Text("Phone: ${user.phoneNumber}")
                Text("Address: ${user.address}")

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Back to Users")
                }
            }
        } ?: Text("User not found")

    }
}