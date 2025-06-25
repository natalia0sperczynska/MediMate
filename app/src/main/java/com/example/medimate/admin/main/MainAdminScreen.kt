package com.example.medimate.admin.main


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
import com.example.medimate.ui.theme.PurpleLight
import com.example.medimate.ui.theme.PurpleMain
import com.example.medimate.ui.theme.White
import com.example.medimate.user.main.LoadingScreen
import com.example.medimate.user.main.MainMenuCard
import com.example.medimate.user.main.MainMenuItem
import com.example.medimate.user.main.MainMenuItems
import com.example.medimate.user.main.SectionDivider
import com.example.medimate.user.main.UserActionsSection
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
    var doctorsCount by remember { mutableStateOf(0) }
    var usersCount by remember { mutableStateOf(0) }
    var appointmentsCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(adminId) {
        if (adminId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadAdminData(adminId)
                    adminName = (data?.getValue("name") ?: "Admin").toString()
                    doctorsCount = firestoreClass.getDoctorsCount()
                    usersCount = firestoreClass.getUsersCount()
                    appointmentsCount = firestoreClass.getAppointmentsCount()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Failed to load admin data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }
    if (isLoading) {
        LoadingScreen()
    } else {
        ModelNavDrawerAdmin(navController, drawerState, scope) {
            AdminScreenContent(
                navController, adminName, doctorsCount = doctorsCount,
                usersCount = usersCount,
                appointmentsCount = appointmentsCount
            )
        }
    }
}

@Composable
fun AdminScreenContent(
    navController: NavController,
    adminName: String,
    doctorsCount: Int,
    usersCount: Int,
    appointmentsCount: Int
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        AdminHeaderCard(adminName)
        SectionDivider()
        Spacer(modifier = Modifier.height(24.dp))
        AdminStatsSection(doctorsCount, usersCount, appointmentsCount)
        SectionDivider()
        Spacer(modifier = Modifier.height(24.dp))
        MainMenuSectionAdmin(navController)
        SectionDivider()
        Spacer(modifier = Modifier.height(24.dp))
        UserActionsSection(navController)
    }
}

@Composable
fun AdminHeaderCard(adminName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleMain),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        color = White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(35.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Admin Profile",
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Welcome back,${adminName}",
                    style = MaterialTheme.typography.titleLarge,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ready to manage?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.7f)
                )
            }
        }
    }
}


@Composable
fun MainMenuSectionAdmin(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Black,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(AdminMenuItems() ) { item ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    MainMenuCard(
                        icon = item.icon,
                        title = item.title,
                        onClick = {
                            when (item.title) {
                                "Manage Users" -> navController.navigate(Screen.ManageUsers.route)
                                "Manage Doctors" ->navController.navigate(Screen.DoctorsAdmin.route)
                                "Edit Doctors Availability" -> navController.navigate(Screen.EditDoctorAvailability.route)
                            }
                        }
                    )
                }
            }
        }
    }
}



fun AdminMenuItems(): List<MainMenuItem> {
    return listOf(
        MainMenuItem(Icons.Default.Settings, "Manage Users"),
        MainMenuItem(Icons.Default.Person, "Manage Doctors"),
        MainMenuItem(Icons.Default.SmartToy, "Edit Doctors Availability"),
    )
}

@Composable
fun AdminMenuCard(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(120.dp, 100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, PurpleLight.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = PurpleMain.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
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
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun AdminStatsSection(doctorsCount: Int, usersCount: Int, appointmentsCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard("Doctors", doctorsCount.toString(), Icons.Default.Person)
        StatCard("Users", usersCount.toString(), Icons.Default.Group)
        StatCard("Appointments", appointmentsCount.toString(), Icons.Default.Event)
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PurpleMain,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PurpleMain
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Black.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAdminScreenPreview() {
    MediMateTheme {
        MainAdminScreen(navController = rememberNavController())
    }
}