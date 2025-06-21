package com.example.medimate.user.updateData

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.healme.R
import com.example.medimate.firebase.storage.storage
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.firebase.user.User
import com.example.medimate.ui.theme.MediMateButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri

@Composable
fun UpdateDataScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestoreClass = UserDAO()
    val context = LocalContext.current
    val userId = auth.currentUser?.uid
    val coroutineScope = rememberCoroutineScope()
    storage = Firebase.storage
    val storageRef = storage.reference


    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var diseases by remember { mutableStateOf("") }
    var medications by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }
    var documents by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { uploadProfilePic(uri, userId, context, coroutineScope, firestoreClass) { url ->
                profileImageUrl = url
            }}
        }
    )

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
                        address = user.address.joinToString(", ")
                        allergies = user.allergies.joinToString(", ")
                        diseases = user.diseases.joinToString(", ")
                        medications = user.medications.joinToString(", ")
                        profileImageUrl = user.profilePictureUrl
                        documents = user.documents.joinToString { "," }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

        if(profileImageUrl.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(
                    data = profileImageUrl,
                    builder = {
                        placeholder(R.drawable.profile_pic)
                        error(R.drawable.profile_pic)
                    }),
                contentDescription = "Profile picture",
                modifier = Modifier.size(120.dp).padding(8.dp)
            )
        }else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default profile",
                modifier = Modifier.size(120.dp)
            )
        }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Surname") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
        OutlinedTextField(value = allergies, onValueChange = { allergies = it }, label = { Text("Allergies") })
        OutlinedTextField(value = diseases, onValueChange = { diseases = it }, label = { Text("Diseases") })
        OutlinedTextField(value = medications, onValueChange = { medications = it }, label = { Text("Medications") })
        OutlinedTextField(value = documents, onValueChange = { documents = it }, label = { Text("Documents") })
        MediMateButton("Change profile picture", onClick = {imagePickerLauncher.launch("image/*")},icon = Icons.Filled.Image)
        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            MediMateButton("Save",onClick = {
                coroutineScope.launch {
                    val updatedData = mapOf(
                        "name" to name,
                        "surname" to surname,
                        "email" to email,
                        "phoneNumber" to phone,
                        "address" to address.split(",").map { it.trim() },
                        "allergies" to allergies.split(",").map { it.trim() },
                        "diseases" to diseases.split(",").map { it.trim() },
                        "medications" to medications.split(",").map { it.trim() },
                        "documents" to documents.split(",").map { it.trim() },
                        "profilePictureUrl" to profileImageUrl
                    )

                    try {
                        if (userId!=null) {
                            firestoreClass.updateUserData(userId, updatedData)
                            Toast.makeText(
                                context,
                                "Data updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            Spacer(modifier = Modifier.width(8.dp))

            MediMateButton("Cancel",onClick = { navController.popBackStack() })
        }
    }
}

fun uploadProfilePic(
    imageUri: Uri,
    userId: String?,
    context: Context,
    coroutineScope: CoroutineScope,
    firestoreClass: UserDAO,
    onSuccess: (String) -> Unit
) {
    if (userId == null) {
        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        return
    }

    coroutineScope.launch {
        try {
            val metadata = storageMetadata {
                contentType = "image/jpeg"
            }
            val storage = Firebase.storage("gs://medimate-79d20.firebasestorage.app")
            val filename = "profile_${System.currentTimeMillis()}"
            val storageRef = storage.reference.child("user_profile_pics/$userId/$filename")
            storageRef.putFile(imageUri, metadata).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            firestoreClass.updateUserData(userId, mapOf(
                "profilePictureUrl" to downloadUrl
            ))
            onSuccess(downloadUrl)
            Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDataScreenPreview() {
    UpdateDataScreen(navController = rememberNavController())
}
