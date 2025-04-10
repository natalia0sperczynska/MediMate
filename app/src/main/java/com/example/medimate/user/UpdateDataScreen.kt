package com.example.medimate.user

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.healme.R
import com.example.medimate.firebase.FireStoreUser
import com.example.medimate.firebase.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun UpdateDataScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestoreClass = FireStoreUser()
    val context = LocalContext.current
    val userId = auth.currentUser?.uid
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var diseases by remember { mutableStateOf("") }
    var medications by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadUserData(userId)
                    if (data != null) {
                        val user = User.fromMap(data)
                        name = user.name ?: ""
                        surname = user.surname ?: ""
                        email = user.email
                        phone = user.phoneNumber
                        address = user.address.values.joinToString(", ")
                        allergies = user.allergies.joinToString(", ")
                        diseases = user.diseases.joinToString(", ")
                        medications = user.medications.joinToString(", ")
                        profileImageUrl = user.profilePictureUrl
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        profileImageUrl.let {
            if (it.isNotEmpty()) {
                Image(painter = painterResource(id = R.drawable.profile_pic), contentDescription = "Profile Image")
            }
        }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Surname") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
        OutlinedTextField(value = allergies, onValueChange = { allergies = it }, label = { Text("Allergies") })
        OutlinedTextField(value = diseases, onValueChange = { diseases = it }, label = { Text("Diseases") })
        OutlinedTextField(value = medications, onValueChange = { medications = it }, label = { Text("Medications") })

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(onClick = {
                coroutineScope.launch {
                    val updatedData = mapOf(
                        "name" to name,
                        "surname" to surname,
                        "email" to email,
                        "phoneNumber" to phone,
                        "address" to address.split(",").map { it.trim() },
                        "allergies" to allergies.split(",").map { it.trim() },
                        "diseases" to diseases.split(",").map { it.trim() },
                        "medications" to medications.split(",").map { it.trim() }
                    )

                    try {
                        firestoreClass.updateUserData(userId!!, updatedData)
                        Toast.makeText(context, "Data updated successfully!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Save")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Cancel")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDataScreenPreview() {
    UpdateDataScreen(navController = rememberNavController())
}
