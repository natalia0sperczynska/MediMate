package com.example.medimate.register

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.*
import com.example.medimate.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

@Composable
fun RegisterScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fireStore = FireStore()

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Register", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Surname") })
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = {},
            label = { Text("Date of Birth") },
            readOnly = true,
            modifier = Modifier.clickable { showDatePicker = true }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            label = { Text("Repeat Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(onClick = {
            coroutineScope.launch {
                registerUser(name, surname, email, dateOfBirth, password, repeatPassword, fireStore, context)
            }
        }) {
            Text("Create an Account")
        }

        Button(onClick = {navController.navigate(Screen.Login.route)}) {
            Text("Already have an account? Login")
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                dateOfBirth = "$day-${month + 1}-$year"
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

private suspend fun registerUser(
    name: String,
    surname: String,
    email: String,
    dateOfBirth: String,
    password: String,
    repeatPassword: String,
    fireStore: FireStore,
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
@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
