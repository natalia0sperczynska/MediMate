package com.example.medimate.firebase.appointment
import androidx.annotation.Keep

@Keep
data class Appointment(
    var id: String = "",
    var doctorId: String="",
    val patientId: String ="",
    var date: String = "",
    var time : String ="",
    var status: Status = Status.EXPECTED,
    var diagnosis: String = "",
    var notes: String = "",
    val url: String = ""
)