package com.example.medimate.firebase
import androidx.annotation.Keep

@Keep
data class Appointment(
    var id: String = "",
    var doctor: Doctor?=Doctor(),
    val patient: User? =User(),
    val date: String = "",
    val status: Status = Status.EXPECTED,
    val diagnosis: String = "",
    val notes: String = "",
    val url: String = ""
)