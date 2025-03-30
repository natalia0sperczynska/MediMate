package com.example.medimate.user
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.healme.R
import com.example.medimate.firebase.Availability
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.Doctor.Companion.generateTimeSlots
import com.example.medimate.firebase.FireStore
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.MediMateTheme
import kotlinx.coroutines.launch

@Composable
fun SingleDoctor(doctor: Doctor, isSelected: Boolean, onDoctorSelected: (Doctor) -> Unit) {
    var expand by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(targetValue = if(expand) 40.dp else 0.dp, label = "")
    Surface(color = MaterialTheme.colorScheme.secondary,
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
    Row (modifier = Modifier.padding(24.dp)) {
        Column(modifier = Modifier.weight(1f).padding(bottom = extraPadding)) {
            Image(painter = painterResource(id = R.drawable.profile_pic), contentDescription = null, modifier = Modifier. requiredSize(50.dp))
            Text(text = "${doctor.name}  ${doctor.surname}")
            Text(doctor.specialisation)
            if (expand) {
                Text("e-mail: ${doctor.email}")
                Text("phone number: ${doctor.phoneNumber}")
                Text("room: ${doctor.room}")
            }
        }
        Column(modifier = Modifier.selectableGroup()) {
            OutlinedButton(onClick = { expand = !expand }) {
                Text(
                    if (expand) "Show less" else "Show more",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            OutlinedButton(onClick = {}) {
                Text(text = "Set the appointment",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
    }
}

@Composable
fun DoctorList(doctors: List<Doctor>,navController: NavController) {
    var selectedDoctor : Doctor? by
    rememberSaveable { mutableStateOf(null) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (doctors.isEmpty()) {
            Text("No doctors available")
        } else
            LazyColumn {
                item{Text("Select a doctor:", color = MaterialTheme.colorScheme.secondary)}
                items(doctors){ doctor ->
                    SingleDoctor(doctor = doctor, isSelected = (selectedDoctor == doctor),
                        onDoctorSelected = { doctor -> selectedDoctor = doctor })
                }
            }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.MainUser.route) }) {
            Text("Go back")
        }

    }
}
@Composable
fun DoctorScreen(navController: NavController) {
    //Pobieranie z firebase dziala zakomentowane zeby dzialal preview
//    val mFireBase = FireStore()
//    var doctors by rememberSaveable { mutableStateOf<List<Doctor>>(emptyList()) }
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            doctors = mFireBase.getAllDoctors()
//        }
//    }
    DoctorList(doctors = getSampleDoctors(), navController = navController)
}

@Preview(showSystemUi = true)
@Composable
fun DoctorsViewPreview() {
    MediMateTheme {
        DoctorScreen(navController = rememberNavController())
    }
    //dto,, przekazac tylko dane wyswietlane
}
fun getSampleDoctors(): List<Doctor> {
    return listOf(
        Doctor(
            id = "1",
            name = "Anna",
            surname = "Kowalska",
            email = "anna.kowalska@example.com",
            phoneNumber = "+48123456789",
            profilePicture = "",
            specialisation = "Cardiologist",
            room = "101",
            availability = Availability(
                monday = generateTimeSlots(),
                tuesday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots(),
                friday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "2",
            name = "Jan",
            surname = "Nowak",
            email = "jan.nowak@example.com",
            phoneNumber = "+48987654321",
            profilePicture = "",
            specialisation = "Neurologist",
            room = "202",
            availability = Availability(
                monday = generateTimeSlots(),
                tuesday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                friday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        )

    )
}

