package com.example.medimate.user.main
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.tests.getSampleDoctors
import com.example.medimate.ui.theme.PurpleGrey3
import com.example.medimate.ui.theme.PurpleLight
import com.example.medimate.ui.theme.PurpleLight2
import com.example.medimate.user.appointments.AppointmentsViewPreview
import com.example.medimate.user.doctorsView.getDoctorList


@Composable
fun MainUserScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val userId = auth.currentUser?.uid
    val firestoreClass = UserDAO()
    var closestAppointment by remember { mutableStateOf<Appointment?>(null)}
    var userName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadUserData(userId)
                    userName = (data?.getValue("name") ?: "User").toString()
                    closestAppointment=firestoreClass.getClosestAppointment(userId)?.also{
                        if(it.id.isEmpty()){
                            throw Exception("Appointment ID is missing")
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    ModelNavDrawerUser(navController,drawerState) {
        ScreenModel(navController, userId.toString(), userName, drawerState,closestAppointment)
    }
}

@Composable
fun ScreenModel(navController: NavController, userId: String, userName: String, drawerState: DrawerState,closestAppointment: Appointment?) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                HeaderCard(userName=userName)
                Spacer(modifier = Modifier.height(24.dp))
                UpcomingAppointmentsCard(closestAppointment, navController = navController)
                Spacer(modifier = Modifier.height(24.dp))
                MainMenuSection(navController = navController,userId)
                Spacer(modifier = Modifier.height(24.dp))
                OurDoctors()
                Spacer(modifier = Modifier.height(16.dp))
                UserActionsSection(navController=navController)
            }
        }

}

@Composable
fun HeaderCard(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleMain),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    contentDescription = "Profile",
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
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = White
                )
                Text(
                    text = "How are you feeling today?",
                    style = MaterialTheme.typography.bodySmall,
                    color = White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
@Composable
fun UpcomingAppointmentsCard(appointment: Appointment?,navController: NavController){
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleLight2),
        shape = RoundedCornerShape(20.dp),
        onClick = {
            appointment?.let {
                navController.navigate(
                    Screen.SingleAppointment.createRoute(it.id
                    )
                )
            } ?: run{
                Toast.makeText(context, "No appointment details available", Toast.LENGTH_SHORT).show()
            }
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Appointment",
                    tint = White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Your upcoming appointment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.8f)
                )
                appointment?.let {
                    Text(
                        text = "Date: ${it.date}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White
                    )
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
    }
}

@Composable
fun MainMenuSection(navController: NavController,userId: String){
    Column { Text(text="Main Menu",
        style = MaterialTheme.typography.headlineSmall,
        color = Black,
        modifier = Modifier.padding(bottom = 16.dp) )}
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(MainMenuItems()) { item ->
            MainMenuCard(
                icon = item.icon,
                title = item.title,
                onClick = {
                    when(item.title) {
                        "Appointments History" -> navController.navigate(Screen.AppointmentsHistory.route)
                        "My Profile" -> navController.navigate(Screen.UserDocumentation.createRoute(userId = userId))
                        "Update Data" -> navController.navigate(Screen.UpdateData.route)
                    }
                }
            )
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
            .size(100.dp)
            .clickable { onClick() },
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
fun OurDoctors() {
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    LaunchedEffect(Unit) { doctors = getDoctorList() }
    Column {
        Text(
            text = "Our Doctors",
            style = MaterialTheme.typography.headlineSmall,
            color = Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items(doctors) { doctor ->
                DoctorCard(doctor = doctor)
            }
        }
    }
}

@Composable
fun DoctorCard(doctor:Doctor){
    Card(modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors=CardDefaults.cardColors(containerColor = PurpleGrey3)){
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)){
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = PurpleMain.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(25.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Doctor",
                    tint = PurpleMain,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctor.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Black
                )
                Text(
                    text = doctor.specialisation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black.copy(alpha = 0.6f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${doctor.rating} (${doctor.reviews} Reviews)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Black.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

fun MainMenuItems(): List<MainMenuItem> {
    return listOf(
        MainMenuItem(Icons.Default.Timelapse, "Appointments History"),
        MainMenuItem(Icons.Default.SmartToy, "My Profile"),
        MainMenuItem(Icons.Default.Settings, "Update Data")
    )
}

@Preview(showSystemUi = true)
@Composable
fun MainUserScreenPreview() {
    MediMateTheme {
       ScreenModel(navController = rememberNavController(), userId = "123", userName = "John", drawerState = rememberDrawerState(DrawerValue.Closed), closestAppointment = null)
    }
}

