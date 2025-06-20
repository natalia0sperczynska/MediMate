package com.example.medimate.user.appointments

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.appointment.Status
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.ui.theme.Green
import com.example.medimate.ui.theme.LightGrey
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.Orange
import com.example.medimate.ui.theme.PurpleDark
import com.example.medimate.ui.theme.PurpleLight2
import com.example.medimate.ui.theme.PurpleMain
import com.example.medimate.ui.theme.Red
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.time.format.TextStyle
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medimate.firebase.appointment.AppointmentDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleAppointment(appointmentId:String,navController: NavController) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var doctorName by remember { mutableStateOf("Loading...") }
    var appointment by remember { mutableStateOf<Appointment?>(null) }
    val UserDAO = UserDAO()
    val firebase = DoctorDAO()
    val AppointmentDAO = AppointmentDAO()
    var isCanceling by remember { mutableStateOf(false) }
    val viewModel: AppointmentsModel = viewModel()
    val events by viewModel.events.collectAsState()
    val offset = Offset(5.0f, 10.0f)
//    doctorName  = "Arnold Super"
//    appointment = Appointment(id="1YKfIeHBLS3DWOQaekZM",doctorId="237shdbjwoe872" ,"kjsdbigbw7r29", "03/04/2024", "10:30",
//   Status.EXPECTED,
//    "Healthy",
//    "No meds prescribed",
//    "url")
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isCanceling) showDialog = false },
            title = { Text("Confirm Cancellation") },
            text = { Text("Are you sure you want to cancel this appointment?") },
            textContentColor = PurpleDark,
            confirmButton = {
                Button(
                    onClick = {
                        isCanceling = true
                        viewModel.cancelAppointment(appointmentId) {
                            isCanceling = false
                            navController.popBackStack()
                        }
                    },
                    enabled = !isCanceling,
                    colors = ButtonColors(containerColor = PurpleMain, contentColor = White,disabledContainerColor= LightGrey, disabledContentColor = PurpleLight2)
                ) {
                    if (isCanceling) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    enabled = !isCanceling,
                    colors = ButtonColors(containerColor = LightGrey, contentColor = White,disabledContainerColor= LightGrey, disabledContentColor = PurpleLight2)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    LaunchedEffect(events) {
        events?.let { event ->
            when (event) {
                is AppointmentEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    viewModel.eventHandled()
                }
                AppointmentEvent.NavigateBack -> {
                    navController.popBackStack()
                    viewModel.eventHandled()
                }
            }
        }
    }
    LaunchedEffect(appointmentId) {
        try{
        val fetchedAppointment = UserDAO.getAppointmentById(appointmentId)
        appointment = fetchedAppointment


        fetchedAppointment?.doctorId?.let { doctorId ->
            val doctor = firebase.getDoctorById(doctorId)
            doctorName = "${doctor?.name} ${doctor?.surname}"

        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error loading details", Toast.LENGTH_SHORT).show()
    }
}
    if (appointment == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading appointment details...")
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Appointment Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PurpleLight2)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(label = "Date:", value = appointment!!.date)
                    InfoRow(label = "Time:", value = appointment!!.time)
                    InfoRow(label = "Doctor:", value = doctorName)
                    InfoRow(
                        label = "Status:",
                        value = appointment!!.status.toString()
                    ) {
                        val color = when (appointment?.status) {
                            Status.EXPECTED -> Orange
                            Status.COMPLETED -> Green
                            Status.CANCELLED -> Red
                            null -> MaterialTheme.colorScheme.error
                            Status.SENDING -> Orange
                            Status.SENT -> Orange
                            Status.DELIVERED -> Orange
                            Status.READ -> Orange
                        }
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = "Status",
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightGrey)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Details",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 24.sp,
                            shadow = Shadow(
                                color = PurpleMain, offset = offset, blurRadius = 3f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (appointment!!.diagnosis.isNotBlank()) {
                        InfoRow(label = "Diagnosis:", value = appointment!!.diagnosis)
                    }

                    if (appointment!!.notes.isNotBlank()) {
                        InfoRow(label = "Notes:", value = appointment!!.notes)
                    }

                    if (appointment!!.url.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW,
                                        appointment!!.url.toUri())
                                    context.startActivity(intent)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "View Documents →",
                                color = PurpleDark,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            if (appointment?.status == Status.EXPECTED) {
                Spacer(modifier = Modifier.height(24.dp))
                MediMateButton(
                    text = "Cancel Appointment",
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            }
        }
    }
}
@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: (@Composable () -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.width(100.dp),
            fontSize = 20.sp
        )
        icon?.invoke()
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            fontSize = 18.sp
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun SingleAppointmentScreenPreview() {
    MediMateTheme {
        SingleAppointment(navController = rememberNavController(), appointmentId = "1YKfIeHBLS3DWOQaekZM")
    }
}