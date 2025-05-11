package com.example.medimate.firebase
import androidx.annotation.Keep

@Keep
data class Appointment(
    var id: String = "",
    var doctorId: String="",
    val patientId: String ="",
    val date: String = "",
    val time : String ="",
    val status: Status = Status.EXPECTED,
    val diagnosis: String = "",
    val notes: String = "",
    val url: String = ""
)