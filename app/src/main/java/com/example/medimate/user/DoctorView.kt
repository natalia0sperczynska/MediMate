package com.example.medimate.user
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.healme.R
import com.example.medimate.firebase.Availability
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.Doctor.Companion.generateTimeSlots
import com.example.medimate.firebase.FireStore
import com.example.medimate.ui.theme.MediMateTheme
import kotlinx.coroutines.launch

@Composable
fun SingleDoctor(doctor: Doctor, isSelected: Boolean, onDoctorSelected: (Doctor) -> Unit) {
    var expand by rememberSaveable { mutableStateOf(false) }
    Row {
        Image(painter = painterResource(id = R.drawable.profile_pic), contentDescription = null)
        Text(text = "${doctor.name}  ${doctor.surname}")
        Text(doctor.specialisation)
        OutlinedButton(onClick = {expand =!expand}){
            Text(if(expand) "Show less" else "Show more")
        }
    }
}

@Composable
fun DoctorList(doctors: List<Doctor>,navController: NavController) {
    var selectedDoctor : Doctor? by
    rememberSaveable { mutableStateOf(null) }
    Column {
        if(doctors.isEmpty()) {
            Text("No doctors available")
        }else
        doctors.forEach { doctor ->
            SingleDoctor(doctor = doctor, isSelected=(selectedDoctor== doctor),
            onDoctorSelected = {doctor -> selectedDoctor = doctor})
        }
    }
}
@Composable
fun DoctorScreen(navController: NavController) {
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
        )
    )
}

