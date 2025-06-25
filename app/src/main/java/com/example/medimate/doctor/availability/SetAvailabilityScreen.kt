
package com.example.medimate.doctor.availability

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medimate.firebase.appointment.Term
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.register.DatePickerModal
import com.example.medimate.ui.theme.Grey2
import com.example.medimate.ui.theme.PurpleLight2
import com.example.medimate.user.appointments.convertMillisToDate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Composable function that displays a screen for doctors to set their availability.
 * Allows selecting a date and toggling time slots as available/unavailable.
 *
 * @param navController The navigation controller for handling screen transitions.
 * @param viewModel The ViewModel managing the availability data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetAvailabilityScreen(
    navController: NavController,
    viewModel: SetAvailabilityModel = remember { SetAvailabilityModel() }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        Toast.makeText(context, "Not authenticated", Toast.LENGTH_SHORT).show()
        navController.popBackStack()
        return
    }

    var selectedDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(doctorId) {
        isLoading = true
        try {
            val doctor = DoctorDAO().getDoctorById(doctorId)
            if (doctor != null) {
                viewModel.setCurrentDoctor(doctor)
            } else {
                Toast.makeText(context, "Doctor not found", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading doctor: ${e.message}", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Availability") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedDate != null) {
                FloatingActionButton(
                    onClick = {
                        isSaving = true
                        coroutineScope.launch {
                            val success = viewModel.saveDayAvailability(context)
                            isSaving = false
                            Toast.makeText(
                                context,
                                if (success) "Availability saved!" else "Failed to save",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (success) {
                                navController.popBackStack()
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
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
                                coroutineScope.launch {
                                    selectedDate?.let { date ->
                                        val terms = viewModel.getTermsForDate(date)
                                        viewModel.loadAvailability(terms, date)
                                    }
                                }
                            }
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                selectedDate?.let { date ->
                    Text(
                        text = "Available Time Slots for $date",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn {
                        itemsIndexed(viewModel.currentAvailability) { index, term ->
                            AvailabilitySlotItem(
                                term = term,
                                onToggle = {
                                    viewModel.toggleTerm(index)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
/**
 * Composable item representing a single availability time slot.
 * Displays the time range and a checkbox to toggle availability.
 *
 * @param term The time slot term to display.
 * @param onToggle Callback when the availability is toggled.
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