package com.example.medimate.doctor.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.AuthManager
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.appointment.AppointmentDAO
import com.example.medimate.firebase.appointment.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for loading and holding the list of future appointments
 * (with status EXPECTED) for the currently logged-in doctor.
 */
class DoctorFutureAppointmentsModel : ViewModel() {
    private val _appointments = MutableStateFlow<List<Appointment>?>(emptyList())
    val appointments: StateFlow<List<Appointment>?> get() = _appointments

    init {
        viewModelScope.launch {
            val doctorId = AuthManager.getCurrentUser()?.id
            _appointments.value = if (doctorId != null) {
                AppointmentDAO().getAppointmentsForDoctor(doctorId)
                    .filter { it.status == Status.EXPECTED }
            } else emptyList()
        }
    }
}
