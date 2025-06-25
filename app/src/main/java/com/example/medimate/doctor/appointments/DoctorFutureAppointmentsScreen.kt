package com.example.medimate.doctor.appointments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medimate.firebase.UserProvider
import com.example.medimate.ui.theme.Purple
import com.example.medimate.doctor.ModelNavDrawerDoctor


/**
 * Screen displaying upcoming (expected) appointments for the logged-in doctor.
 * Pulls data from [DoctorFutureAppointmentsModel] and displays appointment cards.
 */
@Composable
fun DoctorFutureAppointmentsScreen(navController: NavController) {
    val viewModel: DoctorFutureAppointmentsModel = viewModel()
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
                        "Upcoming Appointments",
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
