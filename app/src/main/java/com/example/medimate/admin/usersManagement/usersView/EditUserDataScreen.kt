package com.example.medimate.admin.usersManagement.usersView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medimate.ui.theme.MediMateButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDataScreen(
    navController: NavController,
    userId: String?,
    viewModel: EditUserDataViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) {
        userId?.let { viewModel.loadUserData(it) }
    }

    val userState by viewModel.userState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit User Data") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (userState == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("User not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Personal Information", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = userState?.name ?: "",
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = userState?.surname ?: "",
                    onValueChange = { viewModel.updateSurname(it) },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = userState?.dateOfBirth ?: "",
                    onValueChange = { viewModel.updateDateOfBirth(it) },
                    label = { Text("Date of Birth") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Contact Information", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = userState?.email ?: "",
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                OutlinedTextField(
                    value = userState?.phoneNumber ?: "",
                    onValueChange = { viewModel.updatePhoneNumber(it) },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Text("Address", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value = userState?.address?.get("street") ?: "",
                    onValueChange = { viewModel.updateAddress("street", it) },
                    label = { Text("Street") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = userState?.address?.get("city") ?: "",
                    onValueChange = { viewModel.updateAddress("city", it) },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = userState?.address?.get("postalCode") ?: "",
                    onValueChange = { viewModel.updateAddress("postalCode", it) },
                    label = { Text("Postal Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Medical Information", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = userState?.allergies?.joinToString(", ") ?: "",
                    onValueChange = { viewModel.updateAllergies(it.split(", ")) },
                    label = { Text("Allergies (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userState?.diseases?.joinToString(", ") ?: "",
                    onValueChange = { viewModel.updateDiseases(it.split(", ")) },
                    label = { Text("Diseases (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userState?.medications?.joinToString(", ") ?: "",
                    onValueChange = { viewModel.updateMedications(it.split(", ")) },
                    label = { Text("Medications (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                MediMateButton(
                    text = if (isUpdating) "Updating..." else "Save Changes",
                    onClick = {
                        scope.launch {
                            try {
                                viewModel.saveChanges(userId!!)
                                snackbarHostState.showSnackbar("User updated successfully")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error updating user: ${e.message}")
                            }
                        }
                    },
                    enabled = !isUpdating && !isDeleting,
                    loading = isUpdating,
                    modifier = Modifier.fillMaxWidth()
                )

                MediMateButton(
                    text = if (isDeleting) "Deleting..." else "Delete User",
                    onClick = {
                        scope.launch {
                            try {
                                viewModel.deleteUser(userId!!)
                                snackbarHostState.showSnackbar("User deleted successfully")
                                navController.popBackStack()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error deleting user: ${e.message}")
                            }
                        }
                    },
                    enabled = !isUpdating && !isDeleting,
                    loading = isDeleting,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}