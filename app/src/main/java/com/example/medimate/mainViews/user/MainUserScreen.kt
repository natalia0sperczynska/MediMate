package com.example.medimate.mainViews.user

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.FireStoreUser
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MainUserScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val userId = auth.currentUser?.uid
    val firestoreClass = FireStoreUser()
    var userName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDoctorId by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadUserData(userId)
                    userName = (data?.getValue("name") ?: "User").toString()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = PurpleGrey.copy(alpha = 0.3f),
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.background(PurpleGrey2)) {
                Text("Menu", modifier = Modifier.padding(16.dp), color = Black)
                HorizontalDivider(color = LightGrey)
                NavigationDrawerItem(
                    label = { Text("Appointments", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Appointments.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Doctors", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Doctors.route) }
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier.background(Color.White, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Open Menu", tint = Black)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Good to see you again, $userName!", style = MaterialTheme.typography.headlineMedium, color = Black)
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate(Screen.UpdateData.route) }) {
                    Text("Update Data")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    navController.navigate(Screen.ChatScreen.createRoute(selectedDoctorId))
                })
                {
                    Text("Message Doctor")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }) {
                    Text("Logout")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainUserScreenPreview() {
    MediMateTheme {
        MainUserScreen(navController = rememberNavController())
    }
}
