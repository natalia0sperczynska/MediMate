package com.example.medimate.user.appointments
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.appointment.AppointmentDAO
import com.example.medimate.firebase.AuthManager
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.firebase.appointment.Status
import com.example.medimate.firebase.appointment.Term
import com.example.medimate.register.DatePickerModal
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.user.ModelNavDrawerUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.Calendar
import java.util.Locale


@Composable
fun AppointmentsScreen(navController: NavController, selectedDoctorId: String? = null) {
    val viewModel = viewModel<FutureAppointmentsModel>()
    val doctors by viewModel.doctors.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    var selectedTime by remember { mutableStateOf<Term?>(null) }
    val auth = AuthManager
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }


    val appointment = remember {
        mutableStateOf(
            auth.getCurrentUser()?.let {user->
                Appointment(
                    "",
                    doctorId = selectedDoctorId ?: "",
                    user.id,
                    "",
                    "",
                    Status.EXPECTED,
                    "",
                    "",
                    ""
                )
            }
        )

    }

    ModelNavDrawerUser(navController,drawerState) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding).padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ChoseDoctor(
                        doctors = doctors,
                        selectedDoctorId = appointment.value?.doctorId ?: "",
                        onDoctorSelected = { doctorId ->
                            appointment.value = appointment.value?.copy(doctorId = doctorId)
                        }
                    )

                    DatePickerFieldToModal(
                        label = "Pick a date",
                        onDateSelected = { date ->
                            appointment.value = appointment.value?.copy(date = date)
                        }
                    )
                    val selectedDoctor = doctors.find { it.id == appointment.value?.doctorId }
                    if (selectedDoctor != null && !appointment.value?.date.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .heightIn(max = 300.dp)
                        ) {
                            GetAvailableTerms(
                                doctor = selectedDoctor,
                                date = appointment.value?.date ?: "",
                                onTimeSelected = { term ->
                                    selectedTime = term
                                    appointment.value = appointment.value?.copy(
                                        time = "${term.startTime}-${term.endTime}"
                                    )
                                }
                            )
                        }
                        if (appointment.value?.time.isNullOrBlank()) {
                            Text(
                                "Please select a term",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        Text("Select doctor and date to see available terms")
                    }

                    displayButtons(navController, appointment, snackbarHostState)
                    YourAppointments(appointments)
                }
            }

        }
    }

}
@Composable
fun displayButtons(navController: NavController, appointment: MutableState<Appointment?>, snackbarHostState: SnackbarHostState){
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        MediMateButton(
            text="Cancel",
            onClick = { cancel(appointment,scope,snackbarHostState) },
            modifier = Modifier.padding(16.dp),
            //shape = MaterialTheme.shapes.medium,
            )

        MediMateButton(
            text="Confirm",
            modifier = Modifier.padding(16.dp),
            //shape = MaterialTheme.shapes.medium,
            onClick = {
                confirm(appointment,scope,snackbarHostState)
            },
            enabled = !appointment.value?.doctorId.isNullOrBlank() &&
                    !appointment.value?.date.isNullOrBlank() && !appointment.value?.time.isNullOrBlank()
        )
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked() {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text("") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }
    }
}

@Composable
fun DatePickerFieldToModal( label: String,
                            onDateSelected: (String) -> Unit,modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { convertMillisToDate(it) } ?: "",
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            },
        readOnly = true
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = {
                selectedDate = it
                onDateSelected(convertMillisToDate(it!!))},
            onDismiss = { showModal = false }
        )
    }

}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun ChoseDoctor(doctors: List<Doctor>, selectedDoctorId: String,
                onDoctorSelected: (String) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val selectedDoctor = doctors.find { it.id == selectedDoctorId }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box {
            Row(
                modifier = Modifier
                    .clickable { expanded.value = !expanded.value }
                    .padding(16.dp)
                    .background(Color.LightGray)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = selectedDoctor?.let { "${it.name} ${it.surname}" }
                        ?: "Choose your doctor",
                    color = if (selectedDoctor == null) Color.Gray else Color.Black
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Your Doctor",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                doctors.forEach { doctor ->
                    DropdownMenuItem(
                        text = { Text("${doctor.name} ${doctor.surname}") },
                        onClick = {
                            onDoctorSelected(doctor.id)
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun GetAvailableTerms(
    doctor: Doctor,
    date: String,
    onTimeSelected: (Term) -> Unit
) {
    val availableTerms = getAvailableTermsForDate(doctor, date)
    var selectedTerm by remember { mutableStateOf<Term?>(null) }

    if(availableTerms.isEmpty()){
        Text("No available terms")
    }
    else{
    LazyColumn(
        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp).selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Available terms for $date:", style = MaterialTheme.typography.titleSmall) }
        items(availableTerms) { term ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .selectable(
                        selected = (term == selectedTerm),
                        onClick = {
                            selectedTerm = term
                            onTimeSelected(term)
                        },
                        role = Role.RadioButton
                    )
            ) {
                RadioButton(
                    selected = (term == selectedTerm),
                    onClick = null
                )
                Text(text = "${term.startTime} - ${term.endTime}")
            }
        }
    }
    }
}

fun getAvailableTermsForDate(doctor: Doctor, dateString: String): List<Term> {
    val firestore = FirebaseFirestore.getInstance()
    doctor.availabilityChanges[dateString]?.let { return it.filter{term->term.isAvailable} }
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val date = formatter.parse(dateString) ?: return emptyList()

    val calendar = Calendar.getInstance().apply { time = date }
//    val free=availability.filter{(ts,status)->
//        status== && ts !in booked
//
//        availablityMap[doctos.id]==freeslot
//    }
    val defaultTerms = when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> doctor.availability.monday
        Calendar.TUESDAY -> doctor.availability.tuesday
        Calendar.WEDNESDAY -> doctor.availability.wednesday
        Calendar.THURSDAY -> doctor.availability.thursday
        Calendar.FRIDAY -> doctor.availability.friday
        Calendar.SATURDAY -> doctor.availability.saturday
        Calendar.SUNDAY -> doctor.availability.sunday
        else -> emptyList()
    }
    return defaultTerms.filter { it.isAvailable }
}

fun confirm(appointment: MutableState<Appointment?>, scope: CoroutineScope,
            snackbarHostState: SnackbarHostState, onSuccess: () -> Unit = {}) {
    if(appointment.value?.time.isNullOrBlank()){
        return
    }
    appointment.value?.let { appt ->
        val appointmentToSave = appt.copy(
            id = FirebaseFirestore.getInstance().collection("appointments").document().id
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fireStoreAppointments = AppointmentDAO()
                val doctorDao = DoctorDAO()
                fireStoreAppointments.addAppointment(appointmentToSave)
                val doctor = doctorDao.getDoctorById(appointmentToSave.doctorId)
                if (doctor != null) {
                    doctorDao.updateDoctorAvailability(doctor, appointmentToSave)
                }
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Appointment set successfully!",
                        duration = SnackbarDuration.Short
                    )
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("Confirm", "Error confirming appointment: ${e.message}")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Error: ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }
}
fun cancel(appointment: MutableState<Appointment?>, scope: CoroutineScope, snackbarHostState: SnackbarHostState){
    appointment.value?.let { current ->
        appointment.value=current.copy(
            doctorId = "",
            date = "",
            time = ""
        )

    }
    scope.launch {
        snackbarHostState.showSnackbar(
            message = "Appointment choice canceled",
            duration = SnackbarDuration.Short
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun AppointmentsViewPreview() {
    MediMateTheme {
        AppointmentsScreen(navController = rememberNavController())
    }

}

