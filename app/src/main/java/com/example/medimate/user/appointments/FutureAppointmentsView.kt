package com.example.medimate.user.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.ui.theme.Black
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.Purple
import com.example.medimate.user.ModelNavDrawerUser

@Composable
fun YourFutureAppointmentsScreen(navController: NavController){
    val viewModel = viewModel<FutureAppointmentsModel>()
    val appointments by viewModel.appointments.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModelNavDrawerUser(navController,drawerState) {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Upcoming Appointments",style = MaterialTheme.typography.headlineSmall, color = Purple)
                YourAppointments(appointments)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun YourFutureAppointmentsScreenPreview() {
    MediMateTheme {
        YourFutureAppointmentsScreen(navController = rememberNavController())
    }

}