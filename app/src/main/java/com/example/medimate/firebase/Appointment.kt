package com.example.medimate.firebase
data class Appointment(
    var id: String = "",
    var doctor: Doctor?,
    val patient: User?,
    val date: String = "",
    val status: Status = Status.EXPECTED,
    val diagnosis: String = "",
    val notes: String = "",
    val url: String = ""
)