package com.example.medimate.user.appointments

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.example.medimate.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleAppointment(appointmentId:String,navController: NavController) {
    val context = LocalContext.current
    var doctorName by remember { mutableStateOf("Loading...") }
    var appointment by remember { mutableStateOf<Appointment?>(null) }
    val UserDAO = UserDAO()
    val firebase = DoctorDAO()
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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

            // Medical Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightGrey)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Medical Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

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
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appointment!!.url))
                                    context.startActivity(intent)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "View Documents →",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (appointment!!.status == Status.EXPECTED) {
                MediMateButton(
                    text = "Cancel Appointment",
                    onClick = { /* Handle cancellation */ },
                    modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.width(100.dp)
        )
        icon?.invoke()
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun SingleAppointmentScreenPreview() {
    MediMateTheme {
        SingleAppointment(navController = rememberNavController(), appointmentId = "8376927395")
    }
}