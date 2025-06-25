package com.example.medimate.register

import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.user.User
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.Black
import com.example.medimate.ui.theme.MediMateButton
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
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true }
        ) {
            OutlinedTextField(
                value = dateOfBirth?.let { convertMillisToDate(it) } ?: "",
                onValueChange = {},
                label = { Text("Date of Birth (MM/DD/YYYY)") },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Black,
                    disabledLabelColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                        contentDescription = null
                    )
                }
            )
        }

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

        MediMateButton("Create an Account",onClick = {
            if (dateOfBirth == null) {
                Toast.makeText(context, "Please select a date of birth", Toast.LENGTH_SHORT).show()
                return@MediMateButton
            }
            coroutineScope.launch {
                registerUser(name, surname, email,
                    dateOfBirth!!.toString(), password, repeatPassword, fireStore, context)
            }
        }
        )
        Spacer(modifier = Modifier.height(18.dp))
        TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
            Text("Already have an account? Login here",color= MaterialTheme.colorScheme.secondary)
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { selectedDate ->
                selectedDate?.let {
                    dateOfBirth = it
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

/**
 * Composable modal dialog that shows a Material3 date picker.
 *
 * @param onDateSelected Lambda invoked with the selected date in milliseconds since epoch, or null if none selected.
 * @param onDismiss Lambda invoked when the dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    minDate: Long? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000
            }
        }
    )

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

/**
 * Registers a new user with the given data using Firebase Authentication and Firestore.
 *
 * Performs validation on input fields, checks if the user already exists,
 * creates the user in Firebase Auth, stores user data in Firestore, and signs out.
 * Displays Toast messages on success or failure.
 *
 * @param name The user's first name.
 * @param surname The user's surname.
 * @param email The user's email address.
 * @param dateOfBirth The user's date of birth as a string.
 * @param password The user's password.
 * @param repeatPassword The user's password repeated for confirmation.
 * @param fireStore Instance of UserDAO to interact with Firestore.
 * @param context Android Context for showing Toast messages.
 */
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
            address = listOf(),
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

/**
 * Converts a time in milliseconds since epoch to a formatted date string "MM/dd/yyyy".
 *
 * @param millis Time in milliseconds since epoch.
 * @return Formatted date string in the form "MM/dd/yyyy".
 */
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

/**
 * Preview composable for the RegisterScreen.
 *
 * Displays the registration screen UI in Android Studio preview.
 */
@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MediMateTheme {
        RegisterScreen(navController = rememberNavController())
    }
}
