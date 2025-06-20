package com.example.medimate.admin.doctorsManagement

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
fun EditDoctorDataScreen(
    navController: NavController,
    doctorId: String?,
    viewModel: EditDoctorDataViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(doctorId) {
        doctorId?.let { viewModel.loadDoctorData(it) }
    }

    val doctorState by viewModel.doctorState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Doctor Data") },
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
        } else if (doctorState == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Doctor not found")
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
                    value = doctorState?.name ?: "",
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = doctorState?.surname ?: "",
                    onValueChange = { viewModel.updateSurname(it) },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Contact Information", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = doctorState?.email ?: "",
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                OutlinedTextField(
                    value = doctorState?.phoneNumber ?: "",
                    onValueChange = { viewModel.updatePhoneNumber(it) },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Text("Specialized Information", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = doctorState?.specialisation ?: "",
                    onValueChange = { viewModel.updateSpecialisation(it) },
                    label = { Text("Specialisation") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = doctorState?.room ?: "",
                    onValueChange = { viewModel.updateRoom(it) },
                    label = { Text("Room") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                MediMateButton(
                    text = if (isUpdating) "Updating..." else "Save Changes",
                    onClick = {
                        scope.launch {
                            try {
                                viewModel.saveChanges(doctorId!!)
                                snackbarHostState.showSnackbar("Doctor updated successfully")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error updating doctor: ${e.message}")
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
                                viewModel.deleteDoctor(doctorId!!)
                                snackbarHostState.showSnackbar("Doctor deleted successfully")
                                navController.popBackStack()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error deleting doctor: ${e.message}")
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