
@file:JvmName("AddAdminKt")
package com.example.medimate.admin.doctorsManagement.add

import androidx.compose.material3.CircularProgressIndicator
import com.example.medimate.mail.sendMail
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.admin.ModelNavDrawerAdmin
import com.example.medimate.firebase.admin.Admin
import com.example.medimate.firebase.admin.AdminDAO
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.PurpleMain
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
/**
 * Composable screen for adding a new admin to the MediMate system.
 *
 * This screen allows the current admin to input details of the new admin (name, surname, email, password),
 * create a Firebase Authentication account, store the admin in the Firestore database, and send a confirmation email.
 *
 * UI includes:
 * - Input fields for admin data
 * - Snackbar for success/error feedback
 * - CircularProgressIndicator while loading
 *
 * @param navController Controller used for navigation and drawer interactions.
 */
@Composable
fun AddAdmin(navController: NavController) {
    val adminId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val context = LocalContext.current
    val adminDAO = remember { AdminDAO() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
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
                Text("Add Admin", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Add New Admin", style = MaterialTheme.typography.titleMedium)

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
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )

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

                                val admin = Admin(
                                    id = doctorUid,
                                    name = name,
                                    surname = surname,
                                    email = email
                                )

                                adminDAO.addAdmin(admin)
                                val emailSent = sendMail(
                                    context = context,
                                    recipient = email,
                                    subject = "MediMate Admin Account",
                                    body = """
                                        Hello $name $surname,
                                        
                                        Your MediMate account has just been created!
                                        
                                        Login details:
                                        Email: $email
                                        Password: $password
                                        
                                        Please change your password after first login.
                                        
                                        Best regards,
                                        MediMate Team
                                    """.trimIndent()
                                )
                                if (emailSent) {
                                    snackbarHostState.showSnackbar(
                                        message = "Admin added and email sent successfully!",
                                        duration = SnackbarDuration.Short
                                    )
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = "Admin added but email failed to send",
                                        duration = SnackbarDuration.Long
                                    )
                                }

                                snackbarHostState.showSnackbar(
                                    message = "Admin added successfully!",
                                    duration = SnackbarDuration.Short
                                )

                                name = ""
                                surname = ""
                                email = ""
                                password = ""

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
                    enabled = name.isNotBlank() && surname.isNotBlank() && email.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleMain,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    ),
                ) {
                    Text("Add Admin")
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }
            }

        }

    }
}
