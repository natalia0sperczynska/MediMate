package com.example.medimate.admin.doctorsManagement.editDoctorData

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medimate.admin.ModelNavDrawerAdmin
import com.example.medimate.firebase.appointment.Term
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.register.DatePickerModal
import com.example.medimate.ui.theme.Grey2
import com.example.medimate.ui.theme.PurpleLight2
import com.example.medimate.user.appointments.convertMillisToDate
import kotlinx.coroutines.launch
/**
 * Screen for editing a doctor's availability for a specific date.
 *
 * @param navController The NavController for navigation.
 * @param viewModel The ViewModel managing doctor availability.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDoctorAvailability(
    navController: NavController,
    viewModel: AdminManageAvailabilityViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val doctors by viewModel.doctors.collectAsState()
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDoctorDropdown by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    ModelNavDrawerAdmin(navController, drawerState,coroutineScope) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Manage Doctor Availability") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (selectedDate != null && selectedDoctor != null) {
                    FloatingActionButton(
                        onClick = {
                            isSaving = true
                            coroutineScope.launch {
                                try {
                                    viewModel.saveAvailability()
                                    Toast.makeText(
                                        context,
                                        "Availability updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(Icons.Default.Coronavirus, contentDescription = "Save")
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Doctor",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDoctorDropdown = true },
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedDoctor?.let { "${it.name} ${it.surname}" } ?: "Select Doctor",
                                    color = if (selectedDoctor != null) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Doctor")
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = showDoctorDropdown,
                        onDismissRequest = { showDoctorDropdown = false }
                    ) {
                        doctors.forEach { doctor ->
                            DropdownMenuItem(
                                text = { Text("${doctor.name} ${doctor.surname}") },
                                onClick = {
                                    selectedDoctor = doctor
                                    showDoctorDropdown = false
                                    selectedDate?.let { date ->
                                        viewModel.loadDoctorAvailability(doctor, date)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = selectedDate ?: "Select a date",
                    onValueChange = {},
                    label = { Text("Select Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )

                if (showDatePicker) {
                    DatePickerModal(
                        onDateSelected = { millis ->
                            millis?.let {
                                selectedDate = convertMillisToDate(it)
                                selectedDoctor?.let { doctor ->
                                    viewModel.loadDoctorAvailability(doctor, selectedDate!!)
                                }
                            }
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                selectedDate?.let { date ->
                    selectedDoctor?.let { doctor ->
                        Text(
                            text = "Availability for Dr. ${doctor.name} ${doctor.surname} on $date",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val availability by viewModel.availability.collectAsState()

                        if (availability.isEmpty()) {
                            Text("No availability data for selected date")
                        } else {
                            LazyColumn {
                                itemsIndexed(availability) { index, term ->
                                    AvailabilitySlotItem(
                                        term = term,
                                        onToggle = {
                                            viewModel.toggleTermAvailability(index)
                                        }
                                    )
                                }
                            }
                        }
                    } ?: run {
                        Text("Please select a doctor first")
                    }
                } ?: run {
                    Text("Please select a date")
                }
            }
        }
    }
}
/**
 * UI component representing a single availability slot with toggle functionality.
 *
 * @param term The Term to display.
 * @param onToggle Callback when the term's availability is toggled.
 */
@Composable
fun AvailabilitySlotItem(
    term: Term,
    onToggle: () -> Unit
) {
    val bgColor = if (term.isAvailable) PurpleLight2 else Grey2

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onToggle)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${term.startTime}-${term.endTime}",
                color = Color.White
            )
            Checkbox(
                checked = term.isAvailable,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color.White.copy(alpha = 0.6f),
                    checkmarkColor = PurpleLight2
                )
            )
        }
    }
}