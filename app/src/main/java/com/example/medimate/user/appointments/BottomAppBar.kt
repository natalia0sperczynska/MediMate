package com.example.medimate.user.appointments
import androidx.compose.material3.BottomAppBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.medimate.navigation.Screen
/**
 * A composable that displays a bottom app bar with navigation icons and a floating action button.
 *
 * The bottom app bar contains icons for navigating to:
 * - Main Appointment View
 * - Doctors list
 * - Appointment History
 * - An image-related action (not implemented)
 *
 * The floating action button currently has no defined action.
 *
 * @param navController The NavController used to handle navigation between screens.
 */
@Composable
fun BottomAppBar(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.SingleAppointment.route)}) {
                        Icon(Icons.Filled.Menu, contentDescription = "Main Appointemnt View")
                    }
                    IconButton(onClick = {navController.navigate(Screen.Doctors.route)}) {
                        Icon(
                            Icons.Filled.Person2,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.AppointmentsHistory)}) {
                        Icon(
                            Icons.Filled.History,
                            contentDescription = "History appointments",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "Localized description",
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* do something */ },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
        },
    ) { innerPadding ->
        Text(
            modifier = Modifier.padding(innerPadding),
            text = "Example of a scaffold with a bottom app bar."
        )
    }
}