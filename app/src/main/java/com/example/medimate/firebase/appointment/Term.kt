package com.example.medimate.firebase.appointment

data class Term(
    val startTime: String = "",
    val endTime: String = "",
    var isAvailable: Boolean = true
)
