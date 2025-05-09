package com.example.medimate.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.*
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.MediMateTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RegisterScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf<Long?>(null) }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fireStore = UserDAO()

    Column(modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text("Register", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name")},
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.star_off),
                    contentDescription =null
                )
            }
        )
        Spacer(modifier = Modifier.height(18.dp))
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Surname") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.star_off),
                    contentDescription =null
                )
            }
        )
        Spacer(modifier = Modifier.height(18.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_dialog_email),
                    contentDescription =null
                )
            }
        )

        Spacer(modifier = Modifier.height(18.dp))
        OutlinedTextField(
            value = dateOfBirth?.let { convertMillisToDate(it) } ?: "",
            onValueChange = {},
            label = { Text("Date of Birth") },
            readOnly = true,
            modifier = Modifier.clickable { showDatePicker = true }.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                    contentDescription =null
                )
            }
        )
        Spacer(modifier = Modifier.height(18.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_secure),
                    contentDescription =null
                )
            }
        )
        Spacer(modifier = Modifier.height(18.dp))
        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            label = { Text("Repeat Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_secure),
                    contentDescription =null
                )
            }
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = {
            coroutineScope.launch {
                registerUser(name, surname, email, dateOfBirth?.let { convertMillisToDate(it) } ?: "", password, repeatPassword, fireStore, context)
            }
        },colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary), modifier = Modifier.fillMaxWidth(),shape = MaterialTheme.shapes.large,enabled = name.isNotBlank() && surname.isNotBlank() && email.isNotBlank() && dateOfBirth != null && password.isNotBlank() && repeatPassword.isNotBlank()) {
            Text("Create an Account", color = MaterialTheme.colorScheme.onSecondary)
        }
        Spacer(modifier = Modifier.height(18.dp))
        TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
            Text("Already have an account? Login here",color= MaterialTheme.colorScheme.secondary)
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { dateOfBirth = it },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private suspend fun registerUser(
    name: String,
    surname: String,
    email: String,
    dateOfBirth: String,
    password: String,
    repeatPassword: String,
    fireStore: UserDAO,
    context: android.content.Context
) {
    if (name.isBlank() || surname.isBlank() || email.isBlank() || dateOfBirth.isBlank() || password.isBlank()) {
        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
        return
    }
    if (password != repeatPassword) {
        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val userExists = fireStore.checkIfUserExists(email)
        if (userExists) {
            Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show()
            return
        }

        val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Failed to create user")

        val user = User(
            id = firebaseUser.uid,
            name = name,
            surname = surname,
            email = email,
            dateOfBirth = dateOfBirth,
            phoneNumber = "",
            profilePictureUrl = "",
            address = mapOf(),
            allergies = listOf(),
            diseases = listOf(),
            medications = listOf()
        )

        fireStore.registerOrUpdateUser(user)

        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Account created successfully! Please log in.", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MediMateTheme {
        RegisterScreen(navController = rememberNavController())
    }
}