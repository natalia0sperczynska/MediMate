package com.example.medimate.doctor.appointments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medimate.firebase.UserProvider
import com.example.medimate.ui.theme.Purple
import com.example.medimate.doctor.ModelNavDrawerDoctor
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.firebase.user.User

/**
 * Screen displaying past (completed or cancelled) appointments for the logged-in doctor.
 * Pulls data from [DoctorPastAppointmentsModel] and displays appointment cards.
 */
@Composable
fun DoctorPastAppointmentsScreen(navController: NavController) {
    val viewModel: DoctorPastAppointmentsModel = viewModel()
    val appointments by viewModel.appointments.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    UserProvider { profilePictureUrl ->
        ModelNavDrawerDoctor(navController, drawerState, profilePictureUrl) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        "Past Appointments",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Purple,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                    if (appointments.isNullOrEmpty()) {
                        Text("No appointments", modifier = Modifier.padding(start = 16.dp, top = 32.dp))
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(appointments!!) { appointment ->
                                DoctorAppointmentCard(
                                    appointment = appointment,
                                    onClick = {
                                        navController.navigate("single_appointment/${appointment.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A reusable appointment card used to display basic appointment info for a doctor.
 *
 * @param appointment The [Appointment] to display.
 * @param onClick Lambda to invoke when the card is clicked.
 */
@Composable
fun DoctorAppointmentCard(appointment: Appointment, onClick: () -> Unit) {
    val userDao = remember { UserDAO() }
    val user by produceState<User?>(initialValue = null, key1 = appointment.patientId) {
        value = userDao.getUserById(appointment.patientId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Patient: ${user?.name ?: "Loading..."} ${user?.surname.orEmpty()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Date: ${appointment.date}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Time: ${appointment.time}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
