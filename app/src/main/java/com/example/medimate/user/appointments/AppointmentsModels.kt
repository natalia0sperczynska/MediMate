package com.example.medimate.user.appointments
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.example.medimate.navigation.Screen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.appointment.AppointmentDAO
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.user.doctorsView.getDoctorList
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


open class AppointmentsModel: ViewModel(){
    private val mFireBase = AppointmentDAO()
    private val appointmentDAO = AppointmentDAO()
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    private val _appointments = MutableStateFlow<List<Appointment>?>(emptyList())
    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    private val _events = MutableStateFlow<AppointmentEvent?>(null)

    open val doctors: StateFlow<List<Doctor>> get() = _doctors
    open val appointments: MutableStateFlow<List<Appointment>?> get() = _appointments
    val selectedDoctor: StateFlow<Doctor?> get() = _selectedDoctor
    val events: StateFlow<AppointmentEvent?> get() = _events

    fun eventHandled() {
        _events.value = null
    }

    open fun loadDoctorById(doctorId: String) {
        viewModelScope.launch {
            _selectedDoctor.value = DoctorDAO().getDoctorById(doctorId)
        }
    }
    fun cancelAppointment(appointmentId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser == null) {
                    throw Exception("User not authenticated")
                }
                appointmentDAO.cancelAppointment(appointmentId)
                _events.value = AppointmentEvent.ShowToast("Appointment canceled successfully")
                appointments.value = appointmentDAO.getFutureAppointments()
                onSuccess()
            } catch (e: Exception) {
                _events.value = AppointmentEvent.ShowToast("Appointment cancel failed: ${e.message}")
                Log.e("AppointmentDAO", "Cancellation error", e)
            }
        }
    }
    init {
        viewModelScope.launch {
            _doctors.value = getDoctorList()
        }
    }

}

class FutureAppointmentsModel : AppointmentsModel() {
    init {
        viewModelScope.launch {
            appointments.value = AppointmentDAO().getFutureAppointments()
        }
    }
}


class PastAppointmentsModel : AppointmentsModel(){
    init {
        viewModelScope.launch {
            appointments.value= AppointmentDAO().getPastAppointments()
        }
    }

}


@Composable
fun YourAppointments(appointments: List<Appointment>?,navController:NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        appointments?.let {
            if (it.isEmpty()) {
                Text("No appointments")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Text("Your appointments") }
                    items(appointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onClick = {
                                navController.navigate(
                                    Screen.SingleAppointment.createRoute(appointment.id)
                                )
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment,onClick:()->Unit) {
    val doctorDao = remember { DoctorDAO() }

    val doctor by produceState<Doctor?>(initialValue = null, key1 = appointment.doctorId) {
        value = doctorDao.getDoctorById(appointment.doctorId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Doctor: ${doctor?.name ?: "Loading..."} ${doctor?.surname.orEmpty()}")
            Text(text = "Date: ${appointment.date}")
            Text(text="Time: ${appointment.time}")
        }
    }
}
