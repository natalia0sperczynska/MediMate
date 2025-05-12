package com.example.medimate.user.appointments
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.appointment.AppointmentDAO
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.user.doctorsView.getDoctorList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


open class AppointmentsModel: ViewModel(){
    private val mFireBase = AppointmentDAO()
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
            _appointments.value= mFireBase.getAppointments()
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
fun YourAppointments(appointments: List<Appointment>?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        appointments?.let{
            if(it.isEmpty()) {
                Text("No appointments")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Text("Your appointments") }
                    items(appointments) { appointment ->
                        AppointmentCard(appointment = appointment)
                    }
                }
            }
        }
            ?: Text("Loading appointments..")
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    val doctorDao = remember { DoctorDAO() }

    val doctor by produceState<Doctor?>(initialValue = null, key1 = appointment.doctorId) {
        value = doctorDao.getDoctorById(appointment.doctorId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Doctor: ${doctor?.name ?: "Loading..."} ${doctor?.surname.orEmpty()}")
            Text(text = "Date: ${appointment.date}")
            Text(text="Time: ${appointment.time}")
        }
    }
}