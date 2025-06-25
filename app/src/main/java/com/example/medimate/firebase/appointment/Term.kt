package com.example.medimate.firebase.appointment

/**
 * Data class representing a time slot term for appointments.
 *
 * @property startTime The start time of the term (HH:mm format).
 * @property endTime The end time of the term (HH:mm format).
 * @property isAvailable Whether the term is currently available for booking.
 */
data class Term(
    val startTime: String = "",
    val endTime: String = "",
    var isAvailable: Boolean = true
)
