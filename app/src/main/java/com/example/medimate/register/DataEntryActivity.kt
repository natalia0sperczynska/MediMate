package com.example.medimate.register

import android.app.DatePickerDialog
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.medimate.firebase.*
import com.example.medimate.mainViews.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun DataEntryScreen(navigateToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Register", style = MaterialTheme.typography.h4)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Surname") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(value = dateOfBirth, onValueChange = {}, label = { Text("Date of Birth") }, readOnly = true, modifier = Modifier.clickable { showDatePicker = true })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        OutlinedTextField(value = repeatPassword, onValueChange = { repeatPassword = it }, label = { Text("Repeat Password") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

        Button(onClick = { registerUser(name, surname, email, dateOfBirth, password, repeatPassword) }) {
            Text("Create an Account")
        }

        TextButton(onClick = navigateToLogin) {
            Text("Already have an account? Login")
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            LocalContext.current,
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

@Composable
private fun registerUser(name: String, surname: String, email: String, dateOfBirth: String, password: String, repeatPassword: String) {
    if (password != repeatPassword) {
        Toast.makeText(LocalContext.current, "Passwords do not match", Toast.LENGTH_SHORT).show()
        return
    }
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = task.result!!.user!!
                val firestoreClass = FireStore()
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
                firestoreClass.registerOrUpdateUser(user)
                FirebaseAuth.getInstance().signOut()
            } else {
                Toast.makeText(LocalContext.current, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
}
