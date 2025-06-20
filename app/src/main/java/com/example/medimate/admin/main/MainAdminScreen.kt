package com.example.medimate.admin.main


import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
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
import com.example.medimate.ui.theme.PurpleMain
import com.example.medimate.ui.theme.White
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
    ModelNavDrawerAdmin(navController, drawerState, scope) {
        AdminScreenContent(navController, adminName)
    }
}
@Composable
fun AdminScreenContent(navController: NavController,
                       adminName: String
){  val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AdminHeaderCard(adminName)
        Spacer(modifier = Modifier.height(24.dp))
        AdminMainMenuSection(navController)
        Spacer(modifier = Modifier.height(24.dp))
        AdminActionsSection(navController)
    }
}
@Composable
fun AdminHeaderCard(adminName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleMain),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(30.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Admin Profile",
                    tint = White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.8f)
                )
                Text(
                    text = adminName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = White
                )
                Text(
                    text = "Ready to manage?",
                    style = MaterialTheme.typography.bodySmall,
                    color = White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
@Composable
fun AdminMainMenuSection(navController: NavController) {
    Column {
        Text(
            text = "Admin Menu",
            style = MaterialTheme.typography.headlineSmall,
            color = Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(AdminMenuItems()) { item ->
                AdminMenuCard(
                    icon = item.icon,
                    title = item.title,
                    onClick = { item.onClick(navController) }
                )
            }
        }
    }
}

data class AdminMenuItem(val icon: ImageVector, val title: String, val onClick: (NavController) -> Unit)

fun AdminMenuItems(): List<AdminMenuItem> {
    return listOf(
        AdminMenuItem(Icons.Default.Settings, "Manage Users") { navController ->
            navController.navigate(Screen.ManageUsers.route)
        },
        AdminMenuItem(Icons.Default.Person, "Manage Doctors") { navController ->
            navController.navigate(Screen.DoctorsAdmin.route)
        },
        AdminMenuItem(Icons.Default.SmartToy, "Update Data") { navController ->
            navController.navigate(Screen.UpdateData.route)
        }
    )
}

@Composable
fun AdminMenuCard(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(100.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PurpleMain,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AdminActionsSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MediMateButton(
            text = "Logout",
            onClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            },
            modifier = Modifier.weight(1f)
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