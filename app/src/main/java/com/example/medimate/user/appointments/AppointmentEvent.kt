package com.example.medimate.user.appointments
/** Events for appointment UI actions */
sealed class AppointmentEvent {
    data class ShowToast(val message: String) : AppointmentEvent()
    object NavigateBack : AppointmentEvent()
}