package com.example.medimate.appointments
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.Appointment
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.DoctorDAO
import com.example.medimate.firebase.UserDAO
import com.example.medimate.user.getDoctorList
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

suspend fun getAppointments(): List<Appointment>? {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val mFireBase = UserDAO()
    return if (userId != null) mFireBase.loadAppointments(userId) else emptyList()
}
open class AppointmentsModel: ViewModel(){
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    private val _appointments = MutableStateFlow<List<Appointment>?>(emptyList())
    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)

    open val doctors: StateFlow<List<Doctor>> get() = _doctors
    open val appointments: MutableStateFlow<List<Appointment>?> get() = _appointments
    val selectedDoctor: StateFlow<Doctor?> get() = _selectedDoctor

    open fun loadDoctorById(doctorId: String) {
        viewModelScope.launch {
            _selectedDoctor.value = DoctorDAO().getDoctorById(doctorId)
        }
    }

    init {
        viewModelScope.launch {
            _doctors.value = getDoctorList()
            _appointments.value=getAppointments()
        }
    }
}
