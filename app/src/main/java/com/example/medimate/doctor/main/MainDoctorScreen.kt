package com.example.medimate.doctor.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.medimate.ui.theme.MediMateTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState

import ProfilePicture
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.doctor.ModelNavDrawerDoctor
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.firebase.review.Review
import com.example.medimate.firebase.review.ReviewDAO
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.user.main.SectionDivider
import com.example.medimate.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainDoctorScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val doctorId = auth.currentUser?.uid
    val firestoreClass = DoctorDAO()
    var doctorName by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var closestAppointment by remember { mutableStateOf<Appointment?>(null) }

    LaunchedEffect(doctorId) {
        if (doctorId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadDoctorData(doctorId)
                    doctorName = (data?.getValue("name") ?: "Doctor").toString()
                    profilePictureUrl = data?.get("profilePictureUrl") as? String
                    closestAppointment =
                        firestoreClass.getClosestAppointmentForDoctor(doctorId)?.also {
                            if (it.id.isEmpty()) {
                                throw Exception("Appointment ID is missing")
                            }
                        }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Failed to load doctor data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    ModelNavDrawerDoctor(navController, drawerState, profilePictureUrl) {
        ScreenModelDoctor(
            navController,
            doctorId.toString(),
            doctorName,
            drawerState,
            closestAppointment,
            profilePictureUrl
        )
    }
}

@Composable
fun ScreenModelDoctor(
    navController: NavController,
    doctorId: String,
    doctorName: String,
    drawerState: DrawerState,
    closestAppointment: Appointment?,
    profilePictureUrl: String?
) {
    Surface(color = Color(0xFFF9F9F9)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                DoctorHeaderCard(doctorName, profilePictureUrl)
                SectionDivider()
                NextAppointmentCard(closestAppointment, navController)
                SectionDivider(verticalPadding = 16.dp)
                DoctorMainMenuSection(navController, doctorId)
                SectionDivider()
                RecentReviewsSection(doctorId)
                SectionDivider(color = PurpleMain.copy(alpha = 0.1f))
                DoctorActionsSection(navController)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DoctorHeaderCard(doctorName: String, profilePictureUrl: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleMain),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePicture(
                profilePictureUrl = profilePictureUrl,
                modifier = Modifier.size(70.dp),
                size = 70.dp,
                placeholder = {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                color = White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(35.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(32.dp)
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.8f)
                )
                Text(
                    text = "Dr. $doctorName",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ready for your patients?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun NextAppointmentCard(closestAppointment: Appointment?, navController: NavController) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleLight2),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            // navController.navigate(Screen.SingleAppointment.createRoute(nextAppointment.id))
        }) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            color = White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(35.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Next Patient",
                        tint = White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = "Next appointment",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    closestAppointment?.let {
                        Text(
                            text = it.date,
                            style = MaterialTheme.typography.bodyMedium,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Patient: ${it.patientId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = White.copy(alpha = 0.7f)
                        )
                    } ?: Text(
                        text = "No upcoming appointments",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = White.copy(alpha = 0.05f),
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
            )
        }
    }
}

@Composable
fun DoctorMainMenuSection(navController: NavController, doctorId: String) {
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
            items(DoctorMainMenuItems()) { item ->
                MainMenuCard(
                    icon = item.icon,
                    title = item.title,
                    onClick = { item.onClick(navController, doctorId) }
                )
            }
        }
    }
}

data class MainMenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: (NavController, String) -> Unit
)

fun DoctorMainMenuItems(): List<MainMenuItem> = listOf(
    MainMenuItem(
        Icons.Default.People,
        "My Patients"
    ) { nav, id -> nav.navigate(Screen.UpdateData.route) },
    MainMenuItem(
        Icons.Default.Chat,
        "Chat"
    ) { nav, id -> nav.navigate(Screen.ChatSelection.createRoute(true)) },
    MainMenuItem(
        Icons.Default.People,
        "My Availability"
    ) { nav, id -> nav.navigate(Screen.DoctorAvailability.createRoute(id)) },
    MainMenuItem(Icons.Default.Star, "Reviews") { nav, id -> nav.navigate(Screen.UpdateData.route) }
)

@Composable
fun MainMenuCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
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
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Black,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun RecentReviewsSection(doctorId: String, doctorDAO: DoctorDAO = DoctorDAO()) {
    val coroutineScope = rememberCoroutineScope()
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val firestoreClass = UserDAO()
    var patientName by remember { mutableStateOf("") }
    var reviesDAO = ReviewDAO()

    LaunchedEffect(doctorId) {
        isLoading = true
        errorMessage = null
        try {
            reviews = reviesDAO.getReviewsForDoctor(doctorId)
        } catch (e: Exception) {
            errorMessage = "Failed to load reviews: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Recent Reviews",
            style = MaterialTheme.typography.headlineSmall,
            color = Black
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Error loading reviews",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        } else if (reviews.isEmpty()) {
            Text(
                text = "No reviews yet.",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            reviews.take(3).forEach { review ->
                LaunchedEffect(review.userId) {
                    coroutineScope.launch {
                        try {
                            val data = firestoreClass.loadUserData(review.userId)
                            patientName = (data?.getValue("name") ?: "User").toString()
                        } catch (e: Exception) {
                            Log.e("Message", "${e.message}")
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "“${review.text}”",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "- $patientName",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(review.rate.toInt()) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            if (review.rate % 1 > 0) {
                                Icon(
                                    imageVector = Icons.Default.StarHalf,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = review.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorActionsSection(navController: NavController) {
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
fun MainDoctorScreenPreview() {
    MediMateTheme {
        MainDoctorScreen(navController = rememberNavController())
    }
}
