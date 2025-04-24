package com.example.medimate.user
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.healme.R
import com.example.medimate.firebase.Availability
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.Doctor.Companion.generateTimeSlots
import com.example.medimate.navigation.Screen
import com.example.medimate.tests.getSampleDoctors
import com.example.medimate.ui.theme.MediMateTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Composable function for displaying a single doctor.
 */
@Composable
fun SingleDoctor(doctor: Doctor, isSelected: Boolean, onDoctorSelected: (Doctor) -> Unit, navController: NavController) {
    var expand by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(targetValue = if(expand) 40.dp else 0.dp, label = "")
    Surface(border = BorderStroke(1.dp,MaterialTheme.colorScheme.onPrimary), color = MaterialTheme.colorScheme.secondary,
        shape= MaterialTheme.shapes.medium){
    Row (modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f).padding(bottom = extraPadding)) {
            Image(painter = painterResource(id = R.drawable.profile_pic), contentDescription = null, modifier = Modifier. requiredSize(50.dp))
            Text(text = "${doctor.name}  ${doctor.surname}")
            Text(doctor.specialisation)
            if (expand) {
                Text("e-mail: ${doctor.email}")
                Text("phone number: ${doctor.phoneNumber}")
                Text("room: ${doctor.room}")
            }
        }
        Column(modifier = Modifier.selectableGroup()) {
            OutlinedButton(onClick = { expand = !expand }) {
                Text(
                    if (expand) "Show less" else "Show more",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            OutlinedButton(onClick = {onDoctorSelected(doctor)
            navController.navigate(Screen.AppointmentsDoctor.createRoute(doctor.id))}) {
                Text(text = "Set the appointment",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
    }
}

@Composable
fun DoctorList(doctors: List<Doctor>,navController: NavController) {
    var selectedDoctor : Doctor? by
    remember { mutableStateOf(null) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (doctors.isEmpty()) {
            Text("No doctors available")
        } else
            LazyColumn {
                item{Text("Select a doctor:", color = MaterialTheme.colorScheme.secondary)}
                items(doctors){ doctor ->
                    SingleDoctor(doctor = doctor, isSelected = (selectedDoctor == doctor),
                        onDoctorSelected = { doctor -> selectedDoctor = doctor }, navController = navController)
                }
            }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.MainUser.route) }) {
            Text("Go back")
        }

    }
}
class MainViewModel: ViewModel(){
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private val _doctors=MutableStateFlow(getSampleDoctors())
    val doctors=searchText.combine(_doctors) { text, doctors ->
        if (text.isBlank()) {
            doctors
        } else {
            doctors.filter {
                it.doesMatchSearchQuery(text)
            }
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _doctors.value
        )


    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

}
@Composable
fun DoctorScreen(navController: NavController) {
    //Pobieranie z firebase dziala zakomentowane zeby dzialal preview
//    val mFireBase = FireStore()
//    var doctors by rememberSaveable { mutableStateOf<List<Doctor>>(emptyList()) }
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            doctors = mFireBase.getAllDoctors()
//        }
//    }
    val viewModel = viewModel<MainViewMdeol>()
    Column(modifier = Modifier.padding(16.dp)) {  SearchBar(modifier = Modifier)
        Spacer(modifier = Modifier.height(16.dp))
        DoctorList(doctors = getSampleDoctors(), navController = navController) }

}

@Composable
fun SearchBar(modifier: Modifier = Modifier){
    TextField(value="", onValueChange = {}, trailingIcon = {
        Icon(Icons.Default.Search, contentDescription = null)
    }, placeholder = { Text(stringResource(id=R.string.place_holder_search)) },
        modifier = modifier
        .heightIn(min=56.dp)
        .fillMaxWidth()
        .padding(horizontal = 8.dp))

}



@Preview(showSystemUi = true)
@Composable
fun DoctorsViewPreview() {
    MediMateTheme {
        DoctorScreen(navController = rememberNavController())
    }
    //dto,, przekazac tylko dane wyswietlane
}
