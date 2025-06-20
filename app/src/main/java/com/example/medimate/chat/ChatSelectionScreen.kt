package com.example.medimate.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.firebase.user.User
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.PurpleMain
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ChatSelectionScreen(
    navController: NavController,
    isDoctor: Boolean = false
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val coroutineScope = rememberCoroutineScope()

    var contacts by remember { mutableStateOf<List<Any>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(currentUserId, isDoctor) {
        isLoading = true
        coroutineScope.launch {
            try {
                contacts = if (isDoctor) {
                    DoctorDAO().getPatientsForDoctor(currentUserId)
                        .sortedBy { "${it.name} ${it.surname}" }
                } else {
                    DoctorDAO().getAllDoctors()
                        .filter { it.id != currentUserId }
                        .sortedBy { "${it.name} ${it.surname}" }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error loading contacts: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isDoctor) "Select a patient to message" else "Select a doctor to message",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp),
            color = PurpleMain
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val filteredContacts = contacts.filter {
                when (it) {
                    is User -> "${it.name} ${it.surname}".contains(searchQuery, ignoreCase = true)
                    is Doctor -> "Dr. ${it.name} ${it.surname}".contains(searchQuery, ignoreCase = true)
                    else -> false
                }
            }

            if (filteredContacts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No contacts found", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn {
                    items(filteredContacts) { contact ->
                        ContactCard(
                            contact = contact,
                            onContactSelected = { id ->
                                navController.navigate(Screen.ChatScreen.createRoute(id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    contact: Any,
    onContactSelected: (String) -> Unit
) {
    Card(
        onClick = {
            when (contact) {
                is User -> onContactSelected(contact.id)
                is Doctor -> onContactSelected(contact.id)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                when (contact) {
                    is User -> {
                        Text(
                            text = "${contact.name} ${contact.surname}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Patient",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    is Doctor -> {
                        Text(
                            text = "Dr. ${contact.name} ${contact.surname}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = contact.specialisation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open chat",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatSelectionScreenPreview() {
    MediMateTheme {
        ChatSelectionScreen(
            navController = rememberNavController(),
            isDoctor = false
        )
    }
}