package com.example.medimate.admin.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.admin.ModelNavDrawerAdmin
import com.example.medimate.firebase.admin.AdminDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.Black
import com.example.medimate.ui.theme.LightGrey
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.MediMateTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
@Composable
fun MainAdminScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val adminId = auth.currentUser?.uid
    val firestoreClass = AdminDAO()
    var adminName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(adminId) {
        if (adminId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadAdminData(adminId)
                    adminName = (data?.getValue("name") ?: "Admin").toString()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to load admin data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    AdminScreenContent(navController,adminName,drawerState,scope)
}
@Composable
fun AdminScreenContent(navController: NavController,
                       userName: String,
                       drawerState: DrawerState,
                       scope: CoroutineScope
){  ModelNavDrawerAdmin(navController,drawerState,scope) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Good to see you again, $userName!",
            style = MaterialTheme.typography.headlineMedium,
            color = Black
        )
        Spacer(modifier = Modifier.height(16.dp))


        MediMateButton(text = "Update Data",onClick = { navController.navigate(Screen.UpdateData.route) })
        Spacer(modifier = Modifier.height(8.dp))

        MediMateButton(text="Logout",onClick = {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Main.route) { inclusive = true }
            }
        })
    }


    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(20.dp)
            .background(LightGrey.copy(alpha = 0.5f))
            .clickable { scope.launch { drawerState.open() } }
    )
}

}

@Preview(showBackground = true)
@Composable
fun MainAdminScreenPreview() {
    MediMateTheme {
        MainAdminScreen(navController = rememberNavController())
    }
}