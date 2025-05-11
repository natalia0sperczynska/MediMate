package com.example.medimate.admin
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.AdminDAO
import com.example.medimate.firebase.Doctor
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.user.DoctorList
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ManageDoctors(navController: NavController) {
    val adminId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val adminDAO = remember { AdminDAO() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var doctors by remember { mutableStateOf(listOf<Doctor>()) }

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var specialisation by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }

    ModelNavDrawerAdmin(navController,drawerState,scope) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = specialisation,
            onValueChange = { specialisation = it },
            label = { Text("Specialisation") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = room,
            onValueChange = { room = it },
            label = { Text("Room Number") },
            modifier = Modifier.fillMaxWidth()
        )

        LaunchedEffect(Unit) {
            doctors = adminDAO.getAllDoctors()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Manage Doctors", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Add New Doctor", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = specialisation,
                onValueChange = { specialisation = it },
                label = { Text("Specialisation") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = room,
                onValueChange = { room = it },
                label = { Text("Room Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val doctor = Doctor(
                        id = adminDAO.generateDoctorId(), name = name, surname = surname,
                        email = email,
                        phoneNumber = phoneNumber,
                        specialisation = specialisation,
                        room = room,
                        profilePicture = ""
                    )
                    coroutineScope.launch {
                        try {
                            adminDAO.addDoctor(adminId, doctor)
                            doctors = adminDAO.getAllDoctors()
                            Toast.makeText(context, "Doctor added successfully", Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to add a doctor", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                },
                modifier = Modifier.padding(top = 8.dp),
                enabled = name.isNotBlank() && surname.isNotBlank() && email.isNotBlank()
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_add),
                    contentDescription = null
                )
                Text("Add Doctor")
            }
            Spacer(modifier = Modifier.height(24.dp))

            DoctorList(doctors = doctors, navController = navController)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ManageDoctorsScreenPreview() {
    MediMateTheme {
        ManageDoctors(navController = rememberNavController())
    }
}