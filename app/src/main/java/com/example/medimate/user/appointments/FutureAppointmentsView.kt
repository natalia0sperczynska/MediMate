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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.AuthManager
import com.example.medimate.firebase.UserProvider
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.ui.theme.Black
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.Purple
import com.example.medimate.user.ModelNavDrawerUser
/**
 * Screen composable that displays the user's upcoming (future) appointments.
 *
 * It collects the future appointments from the [FutureAppointmentsModel] ViewModel and
 * displays them inside a column with a title. The UI includes a navigation drawer handled by
 * [ModelNavDrawerUser].
 *
 * @param navController The NavController used to handle navigation events.
 */
@Composable
fun YourFutureAppointmentsScreen(navController: NavController) {
    val viewModel = viewModel<FutureAppointmentsModel>()
    val appointments by viewModel.appointments.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    UserProvider { profilePictureUrl ->
        ModelNavDrawerUser(navController, drawerState, profilePictureUrl) {
            Surface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Upcoming Appointments",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Purple
                    )
                    YourAppointments(appointments, navController)
                }
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