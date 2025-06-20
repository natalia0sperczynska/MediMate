package com.example.medimate.user.appointments

sealed class AppointmentEvent {
    data class ShowToast(val message: String) : AppointmentEvent()
    object NavigateBack : AppointmentEvent()
}