package com.example.medimate.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.healme.R
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.FireStore
import com.example.medimate.mainViews.MainScreen
import com.example.medimate.ui.theme.MediMateTheme

@Composable
fun SingleDoctor(doctor: Doctor, isSelected: Boolean, onDoctorSelected: (Doctor) -> Unit) {
    Row {
        Image(painter = painterResource(id = R.drawable.profile_pic), contentDescription = null)
        Text(text = "${doctor.name} + ${doctor.surname}")
        Text(doctor.specialisation)
        RadioButton(false, onClick = {})
    }
}

@Composable
fun DoctorList(doctors: List<Doctor>) {
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

@Preview(showSystemUi = true)
@Composable
fun DoctorsViewPreview() {
    val mFireBase = FireStore()
    MediMateTheme {
        DoctorList(doctors = emptyList())
    }
}
