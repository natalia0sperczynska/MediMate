package com.example.medimate.doctor.updateData

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.MediMateButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun UpdateDataDoctor(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val doctorDAO = remember { DoctorDAO() }
    val context = LocalContext.current
    val doctorId = auth.currentUser?.uid
    val coroutineScope = rememberCoroutineScope()
    val storage = Firebase.storage
    val storageRef = storage.reference

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var specialisation by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { uploadProfilePic(uri, doctorId, context, coroutineScope, doctorDAO) { url ->
                profilePictureUrl = url
            }}
        }
    )

    LaunchedEffect(doctorId) {
        if (doctorId != null) {
            coroutineScope.launch {
                try {
                    val data = doctorDAO.loadDoctorData(doctorId)
                    if (data != null) {
                        name = data["name"] as? String ?: ""
                        surname = data["surname"] as? String ?: ""
                        email = data["email"] as? String ?: ""
                        phone = data["phoneNumber"] as? String ?: ""
                        specialisation = data["specialisation"] as? String ?: ""
                        room = data["room"] as? String ?: ""
                        profilePictureUrl = data["profilePicture"] as? String ?: ""
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error loading doctor data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (profilePictureUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(profilePictureUrl),
                    contentDescription = "Profile picture",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default profile",
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        MediMateButton(
            text = "Change Profile Picture",
            onClick = { imagePickerLauncher.launch("image/*") },
            icon = Icons.Filled.Image,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Surname") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
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

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MediMateButton(
                text = "Cancel",
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            MediMateButton(
                text = "Save",
                onClick = {
                    coroutineScope.launch {
                        val updatedData = mapOf(
                            "name" to name,
                            "surname" to surname,
                            "email" to email,
                            "phoneNumber" to phone,
                            "specialisation" to specialisation,
                            "room" to room,
                            "profilePicture" to profilePictureUrl
                        )

                        try {
                            if (doctorId != null) {
                                doctorDAO.updateDoctorData(doctorId, updatedData)
                                Toast.makeText(
                                    context,
                                    "Doctor data updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Failed to update data: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Uploads a profile picture to Firebase Storage and updates doctor's profile in Firestore
 */
private fun uploadProfilePic(
    imageUri: Uri,
    doctorId: String?,
    context: Context,
    coroutineScope: CoroutineScope,
    doctorDAO: DoctorDAO,
    onSuccess: (String) -> Unit
) {
    if (doctorId == null) {
        Toast.makeText(context, "Doctor not logged in", Toast.LENGTH_SHORT).show()
        return
    }

    coroutineScope.launch {
        try {
            val storage = Firebase.storage("gs://medimate-79d20.firebasestorage.app")
            val filename = "doctor_profile_${System.currentTimeMillis()}"
            val storageRef = storage.reference.child("doctor_profile_pics/$doctorId/$filename")

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            doctorDAO.updateDoctorData(doctorId, mapOf("profilePicture" to downloadUrl))
            onSuccess(downloadUrl)

            Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDoctorDataScreenPreview() {
    UpdateDataDoctor(navController = rememberNavController())
}