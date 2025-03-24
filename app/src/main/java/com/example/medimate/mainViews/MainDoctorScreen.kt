package com.example.medimate.mainViews

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.FireStore
import com.example.medimate.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MainDoctorScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val doctorId = auth.currentUser?.uid
    val firestoreClass = FireStore()
    var doctorName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(doctorId) {
        if (doctorId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadDoctorData(doctorId)
                    doctorName = (data?.getValue("name") ?: "Doctor").toString()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to load doctor data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome back, Dr. $doctorName!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate(Screen.UpdateData.route) }) {
            Text("Update Data")
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

@Preview(showBackground = true)
@Composable
fun MainDoctorScreenPreview() {
    MainDoctorScreen(navController = rememberNavController())
}