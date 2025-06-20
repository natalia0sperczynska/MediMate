@file:JvmName("AddDoctorKt")

package com.example.medimate.admin.doctorsManagement.add

import android.R
import androidx.compose.material3.CircularProgressIndicator
import com.example.medimate.mail.sendMail
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.admin.ModelNavDrawerAdmin
import com.example.medimate.firebase.admin.AdminDAO
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.MediMateTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AddDoctor(navController: NavController) {
    val adminId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val context = LocalContext.current
    val adminDAO = remember { AdminDAO() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var doctors by remember { mutableStateOf(listOf<Doctor>()) }

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var specialisation by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    ModelNavDrawerAdmin(navController, drawerState, scope) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Add Doctor", style = MaterialTheme.typography.headlineMedium)

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
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                LaunchedEffect(Unit) {
                    doctors = adminDAO.getAllDoctors()
                }
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                isLoading = true
                                val authResult = FirebaseAuth.getInstance()
                                    .createUserWithEmailAndPassword(email, password)
                                    .await()
                                val doctorUid = authResult.user?.uid
                                    ?: throw Exception("Failed to get user UID")

                                val doctor = Doctor(
                                    id = doctorUid,
                                    name = name,
                                    surname = surname,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    specialisation = specialisation,
                                    room = room,
                                    profilePicture = ""
                                )

                                adminDAO.addDoctor(adminId, doctor)

                                snackbarHostState.showSnackbar(
                                    message = "Doctor added successfully!",
                                    duration = SnackbarDuration.Short
                                )

                                name = ""
                                surname = ""
                                email = ""
                                phoneNumber=""
                                room=""
                                specialisation=""
                                password=""

                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    message = "Error: ${e.message}",
                                    duration = SnackbarDuration.Long
                                )
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    enabled = name.isNotBlank() && surname.isNotBlank() && email.isNotBlank()
                ) {
                    Text("Add Doctor")
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    MediMateButton(
                        text = "Send Test Email",
                        onClick = {
                            scope.launch {
                                val success = sendMail(
                                    context = context,
                                    recipient = "sperczynskanatalia@gmail.com",
                                    subject = "MediMate Test",
                                    body = "Hello join our app please!!:((("
                                )

                                Toast.makeText(
                                    context,
                                    if (success) "Sent!" else "Failed - check logs",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        })
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ManageDoctorsScreenPreview() {
    MediMateTheme {
        AddDoctor(navController = rememberNavController())
    }
}