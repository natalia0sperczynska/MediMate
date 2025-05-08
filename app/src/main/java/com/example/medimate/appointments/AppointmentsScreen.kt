package com.example.medimate.appointments
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.Appointment
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.FireStoreAppointments
import com.example.medimate.firebase.Status
import com.example.medimate.firebase.Term
import com.example.medimate.navigation.Screen
import com.example.medimate.register.DatePickerModal
import com.example.medimate.ui.theme.MediMateTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.Calendar
import java.util.Locale

@Composable
fun AppointmentsScreen(navController: NavController, selectedDoctor: Doctor? = null) {
    val viewModel = viewModel<AppointmentsModel>()
    val doctors by viewModel.doctors.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val appointment = remember {
        mutableStateOf(
            Appointment(
                "",
                doctor = selectedDoctor,
                null,
                "",
                Status.EXPECTED,
                "",
                "",
                ""
            )
        )
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ChoseDoctor(
                doctors = doctors,
                appointment = appointment
            )

            DatePickerFieldToModal(
                label = "Pick a date",
                onDateSelected = { date ->
                    appointment.value = appointment.value.copy(date = date)
                }
            )
            if (appointment.value.doctor != null && appointment.value.date.isNotBlank()) {
                GetAvailableTerms(
                    doctor = appointment.value.doctor!!,
                    date = appointment.value.date
                )
            } else {
                Text("No available terms yet")
            }
            Button(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                onClick = { navController.navigate(Screen.MainUser.route) }) {
                Text("Cancel")
            }
            Button(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                onClick = { confirm(appointment) },
               // enabled = appointment.doctor != null && appointment.date != ""
            ) {
                Text("Confirm")
            }
            YourAppointments(appointments)
        }

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
fun ChoseDoctor(doctors: List<Doctor>, appointment: MutableState<Appointment>) {
    val expanded = remember { mutableStateOf(false) }
    val currentValue = remember { mutableStateOf(appointment.value.doctor) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box {
            Row(
                modifier = Modifier
                    .clickable { expanded.value = !expanded.value }
                    .padding(16.dp)
                    .background(Color.LightGray)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = currentValue.value?.let { "${it.name} ${it.surname}" }
                    ?: "Choose your doctor",
                    color = if (currentValue.value == null) Color.Gray else Color.Black)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "You Doctor",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                Text("Doctors:", color = MaterialTheme.colorScheme.secondary)
                doctors.forEach { doctor ->
                    DropdownMenuItem(
                        text = { Text("${doctor.name} ${doctor.surname}") },
                        onClick = {
                            currentValue.value = doctor
                            expanded.value = false
                            appointment.value = appointment.value.copy(doctor = doctor)

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GetAvailableTerms(doctor: Doctor, date: String) {
    println("Selected doctor: ${doctor.name}")
    println("Selected date: $date")
    val availableterms =  getAvailableTermsForDate(doctor, date)
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(if (availableterms.isNotEmpty()) availableterms[0] else null)
    }
    if (availableterms.isNotEmpty() && selectedOption != null) {
        LazyColumn(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.selectableGroup()
        ) {
            item { Text("Available terms for $date:") }
            items(availableterms) { term ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .selectable(
                            selected = (term == selectedOption),
                            onClick = { onOptionSelected(term) },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (term == selectedOption), onClick = null
                    )
                    Text(text = "${term.startTime} - ${term.endTime}")
                }
            }
        }
    } else {
        Text("No available terms")
    }
}

@Composable
fun YourAppointments(appointments: List<Appointment>?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (appointments!!.isEmpty()) {
            Text("No future appointments")
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
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Doctor: ${appointment.doctor?.name} ${appointment.doctor?.surname}")
            Text(text = "Date: ${appointment.date}")
        }
    }
}
fun getAvailableTermsForDate(doctor: Doctor, dateString: String): List<Term> {
    doctor.availabilityChanges[dateString]?.let { return it }
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val date = formatter.parse(dateString) ?: return emptyList()

    val calendar = Calendar.getInstance().apply { time = date }
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> doctor.availability.monday
        Calendar.TUESDAY -> doctor.availability.tuesday
        Calendar.WEDNESDAY -> doctor.availability.wednesday
        Calendar.THURSDAY -> doctor.availability.thursday
        Calendar.FRIDAY -> doctor.availability.friday
        Calendar.SATURDAY -> doctor.availability.saturday
        Calendar.SUNDAY -> doctor.availability.sunday
        else -> emptyList()
    }
}

fun confirm(appointment: MutableState<Appointment>) {
    val appointmentToSave = appointment.value.copy(
        id=FirebaseFirestore.getInstance().collection("appointments").document().id
    )
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val fireStoreAppointments = FireStoreAppointments()
            fireStoreAppointments.addAppointment(appointmentToSave)
        } catch (e: Exception) {
            Log.e("Confirm", "Error confirming appointment: ${e.message}")
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun AppointmentsViewPreview() {
    MediMateTheme {
        AppointmentsScreen(navController = rememberNavController())
    }
}

