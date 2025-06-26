package com.example.medimate.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.healme.R
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Composable login screen with Firebase authentication.
 *
 * Handles login for admin, doctor, and user roles.
 *
 * @param navController Navigation controller to handle screen transitions.
 */
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val icon = if (passwordVisible)
        painterResource(id = android.R.drawable.ic_secure)
    else
        painterResource(id = android.R.drawable.ic_menu_view)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondary
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
                    contentDescription = null
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_secure),
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = icon, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let { firebaseUser ->
                                val db = Firebase.firestore
                                val uid = firebaseUser.uid

                                val adminTask = db.collection("admins").document(uid).get()
                                val doctorTask = db.collection("doctors").document(uid).get()
                                val userTask = db.collection("users").document(uid).get()

                                Tasks.whenAll(adminTask, doctorTask, userTask)
                                    .addOnSuccessListener {
                                        val adminDoc = adminTask.result
                                        val doctorDoc = doctorTask.result
                                        val userDoc = userTask.result

                                        isLoading = false
                                        when {
                                            adminDoc.exists() ->
                                                navController.navigate(Screen.MainAdmin.route)

                                            doctorDoc.exists() ->
                                                navController.navigate(Screen.MainDoctor.route)

                                            userDoc.exists() ->
                                                navController.navigate(Screen.MainUser.route)

                                            else -> {
                                                Toast.makeText(
                                                    context,
                                                    "Account not configured",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                FirebaseAuth.getInstance().signOut()
                                            }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Error checking user roles: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain,
                contentColor = White,
                disabledContainerColor = PurpleGrey2,
                disabledContentColor = White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = White)
            } else {
                Text("Login", color = White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
            Text(
                "Don't have an account? Register here",
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Preview function for displaying the LoginScreen in Android Studio.
 */
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginScreenPreview() {
    MediMateTheme {
        LoginScreen(navController = rememberNavController())
    }
}
