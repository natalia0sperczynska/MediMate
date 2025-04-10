package com.example.medimate.appointments
import androidx.compose.foundation.lazy.items

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.Appointment
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.Status
import com.example.medimate.firebase.User
import com.example.medimate.register.DatePickerModal
import com.example.medimate.ui.theme.MediMateTheme
import java.sql.Date
import java.util.Locale


@Composable
fun AppointmentsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Top,horizontalAlignment = Alignment.CenterHorizontally) {
        DisplayJSpinner()
        YourAppointments()
    }
    DatePickerDocked()
    DatePickerFieldToModal()

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
fun DatePickerFieldToModal(modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { convertMillisToDate(it) } ?: "",
        onValueChange = { },
        label = { Text("Select date") },
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
            }
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = { selectedDate = it },
            onDismiss = { showModal = false }
        )
    }

}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun DisplayJSpinner(){
    val parentOptions = listOf("Option 1", "Option 2", "Option 3")
    var expandedState by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(parentOptions[0])}
    var mContex = LocalContext.current
}
//DropdownMenu in Jetpack Compose | Spinner Jetpack ...

@Composable
fun YourAppointments(){
    val appointments = getSampleAppointments()
    LazyColumn(verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally){
        //Spacer(20.dp)
        item { Text("Your appointments") }
        items(appointments) { appointment ->
            AppointmentCard(appointment = appointment)
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Doctor: ${appointment.doctor.name} ${appointment.doctor.surname}")
            Text(text = "Date: ${appointment.date}")
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

fun getSampleAppointments(): List<Appointment> {
    val doctor = Doctor(
        id = "doc001",
        name = "Arnold",
        surname = "Schwarzenegger",
        email = "arnold@doctor.pl",
        phoneNumber = "123456789",
        profilePicture = "https://example.com/arnold.jpg",
        specialisation = "Cardiology",
        room = "101"
    )

    val patient1 = User(
        id = "pat001",
        name = "Alice",
        surname = "Johnson",
        email = "alice@example.com",
        phoneNumber = "987654321",
        dateOfBirth = "1990-04-12",
        profilePictureUrl = "https://example.com/alice.jpg",
        address = mapOf("street" to "Main St", "city" to "Warsaw"),
        allergies = listOf("Penicillin"),
        diseases = listOf("Hypertension"),
        medications = listOf("Atenolol")
    )

    val patient2 = User(
        id = "pat002",
        name = "Bob",
        surname = "Smith",
        email = "bob@example.com",
        phoneNumber = "876543210",
        dateOfBirth = "1985-09-27",
        profilePictureUrl = "https://example.com/bob.jpg",
        address = mapOf("street" to "Oak Rd", "city" to "Krakow"),
        allergies = listOf("None"),
        diseases = listOf("Asthma"),
        medications = listOf("Ventolin")
    )

    return listOf(
        Appointment(
            id = "appt001",
            doctor = doctor,
            patient = patient1,
            date = "2025-04-12 10:30",
            status = Status.EXPECTED,
            diagnosis = "",
            notes = "Patient reported chest pain",
            url = "https://meet.example.com/arnold-alice"
        ),
        Appointment(
            id = "appt002",
            doctor = doctor,
            patient = patient2,
            date = "2025-04-13 14:00",
            status = Status.COMPLETED,
            diagnosis = "Asthma attack",
            notes = "Prescribed new inhaler dosage",
            url = "https://meet.example.com/arnold-bob"
        )
    )
}