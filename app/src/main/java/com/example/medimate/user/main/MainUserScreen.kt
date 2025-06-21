package com.example.medimate.user.main

import ProfilePicture
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.firebase.review.ReviewDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.Black
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.PurpleMain
import com.example.medimate.ui.theme.White
import com.example.medimate.user.ModelNavDrawerUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.firebase.review.Review
import com.example.medimate.tests.getSampleDoctors
import com.example.medimate.ui.theme.PurpleGrey3
import com.example.medimate.ui.theme.PurpleLight
import com.example.medimate.ui.theme.PurpleLight2
import com.example.medimate.user.appointments.AppointmentsViewPreview
import com.example.medimate.user.doctorsView.getDoctorList
import com.example.medimate.user.reviews.ReviewItem
import kotlinx.coroutines.currentCoroutineContext


@Composable
fun MainUserScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val userId = auth.currentUser?.uid
    val firestoreClass = UserDAO()
    var closestAppointment by remember { mutableStateOf<Appointment?>(null) }
    var userName by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadUserData(userId)
                    userName = (data?.getValue("name") ?: "User").toString()
                    profilePictureUrl = data?.get("profilePictureUrl") as? String
                    closestAppointment = firestoreClass.getClosestAppointment(userId)?.also {
                        if (it.id.isEmpty()) {
                            throw Exception("Appointment ID is missing")
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Failed to load user data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    ModelNavDrawerUser(navController, drawerState, profilePictureUrl = profilePictureUrl) {
        ScreenModel(navController, userId.toString(), userName, drawerState, closestAppointment, profilePictureUrl = profilePictureUrl)
    }
}

@Composable
fun ScreenModel(
    navController: NavController,
    userId: String,
    userName: String,
    drawerState: DrawerState,
    closestAppointment: Appointment?,
    profilePictureUrl: String?
) {
    Surface(color = Color(0xFFF9F9F9)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            HeaderCard(navController,userId=userId,userName = userName,profilePictureUrl)
            SectionDivider()
            UpcomingAppointmentsCard(closestAppointment, navController = navController)
            SectionDivider(verticalPadding = 16.dp)
            MainMenuSection(navController = navController, userId)
            SectionDivider()
            OurDoctors(navController = navController)
            SectionDivider(color = PurpleMain.copy(alpha = 0.1f))
            UserActionsSection(navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

@Composable
fun HeaderCard(navController: NavController,userId: String,userName: String,profilePictureUrl:String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleMain),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = {navController.navigate(Screen.UserDocumentation.createRoute(userId = userId))}
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
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "How are you feeling today?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun UpcomingAppointmentsCard(appointment: Appointment?, navController: NavController) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleLight2),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            appointment?.let {
                navController.navigate(
                    Screen.SingleAppointment.createRoute(
                        it.id
                    )
                )
            } ?: run {
                Toast.makeText(context, "No appointment details available", Toast.LENGTH_SHORT)
                    .show()
            }
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
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Appointment",
                        tint = White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = "Your upcoming appointment",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    appointment?.let {
                        Text(
                            text = it.date,
                            style = MaterialTheme.typography.bodyMedium,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "With: Dr. ${it.doctorId}",
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
fun MainMenuSection(navController: NavController, userId: String) {
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
            items(MainMenuItems()) { item ->
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
                                "Appointments History" -> navController.navigate(Screen.AppointmentsHistory.route)
                                "My Profile" -> navController.navigate(
                                    Screen.UserDocumentation.createRoute(
                                        userId = userId
                                    )
                                )

                                "Update Data" -> navController.navigate(Screen.UpdateData.route)
                                "Medical facts" -> navController.navigate(Screen.UpdateData.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis

            )
        }
    }
}

@Composable
fun UserActionsSection(navController: NavController) {
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

@Composable
fun OurDoctors(navController: NavController) {
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    LaunchedEffect(Unit) { doctors = getDoctorList() }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Our Doctors",
                style = MaterialTheme.typography.headlineSmall,
                color = Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            MediMateButton(
                "Message Doctor",
                onClick = { navController.navigate(Screen.ChatSelection.createRoute(false)) })

        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(min =200.dp, max=600.dp)
        ) {
            items(doctors) { doctor ->
                DoctorCard(doctor = doctor, navController = navController)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor, navController: NavController) {
    var showReviews by remember { mutableStateOf(false) }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val reviewDao = remember { ReviewDAO() }
    val context = LocalContext.current

    LaunchedEffect(showReviews) {
        if (showReviews && reviews.isEmpty()) {
            isLoading = true
            try {
                reviews = reviewDao.GetReviewsForDoctor(doctor.id)
                if (reviews.isEmpty()) {
                    Toast.makeText(context, "No reviews found for this doctor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading reviews: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = PurpleMain.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Doctor",
                        tint = PurpleMain,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dr. ${doctor.name}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Black
                    )
                    Text(
                        text = doctor.specialisation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Black.copy(alpha = 0.7f)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable{showReviews =!showReviews}
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${doctor.rating}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Black.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            MediMateButton(
                "Message This Doctor",
                onClick = { navController.navigate(Screen.ChatScreen.createRoute(doctor.id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            AnimatedVisibility(
                visible = showReviews,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        if (reviews.isEmpty()) {
                            Text(
                                text = "No reviews yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            Text(
                                text = "Patient's reviews",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            reviews.forEach { review ->
                                ReviewItem(review = review)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun MainMenuItems(): List<MainMenuItem> {
    return listOf(
        MainMenuItem(Icons.Default.Timelapse, "Appointments History"),
        MainMenuItem(Icons.Default.SmartToy, "My Profile"),
        MainMenuItem(Icons.Default.Settings, "Update Data"),
        MainMenuItem(Icons.Default.Star, "Medical facts")
    )
}
@Composable
fun SectionDivider(modifier: Modifier = Modifier,
                   color: Color = PurpleLight.copy(alpha = 0.2f),
                   thickness: Dp = 1.dp,
                   verticalPadding: Dp = 8.dp) {
    Divider(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding),
        color = color,
        thickness = thickness,
    )
}

@Preview(showSystemUi = true)
@Composable
fun MainUserScreenPreview() {
    MediMateTheme {
        ScreenModel(
            navController = rememberNavController(),
            userId = "123",
            userName = "John",
            drawerState = rememberDrawerState(DrawerValue.Closed),
            closestAppointment = null,
            profilePictureUrl = ""
        )
    }
}

