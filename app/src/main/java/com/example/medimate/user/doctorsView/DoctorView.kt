package com.example.medimate.user.doctorsView

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.medimate.firebase.AuthManager
import com.example.medimate.firebase.UserProvider
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.PurpleDark
import com.example.medimate.ui.theme.White
import com.example.medimate.user.ModelNavDrawerUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Composable function for displaying a single doctor.
 */
@Composable
fun SingleDoctor(
    doctor: Doctor,
    isSelected: Boolean,
    onDoctorSelected: (String) -> Unit,
    navController: NavController
) {
    var expand by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(targetValue = if (expand) 40.dp else 0.dp, label = "")
    Surface(
        border = BorderStroke(1.dp, White), color = PurpleDark,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding)) {
                Image(
                    painter = painterResource(id = R.drawable.profile_pic),
                    contentDescription = null,
                    modifier = Modifier.requiredSize(50.dp)
                )
                Text(text = "${doctor.name}  ${doctor.surname}", color = White)
                Text(doctor.specialisation, color = White)
                if (expand) {
                    Text("e-mail: ${doctor.email}", color = White)
                    Text("phone number: ${doctor.phoneNumber}", color = White)
                    Text("room: ${doctor.room}", color = White)
                }
            }
            Column(modifier = Modifier.selectableGroup()) {
                OutlinedButton(onClick = { expand = !expand }) {
                    Text(
                        if (expand) "Show less" else "Show more",
                        color = White
                    )
                }
                OutlinedButton(onClick = {
                    onDoctorSelected(doctor.id)
                    navController.navigate(Screen.AppointmentsDoctor.createRoute(doctor.id))
                }) {
                    Text(
                        text = "Set the appointment",
                        color = White
                    )
                }
                OutlinedButton(onClick = {
                    onDoctorSelected(doctor.id)
                    navController.navigate(Screen.DoctorReviewScreen.createRoute(doctor.id))
                }) {
                    Text(
                        text = "Reviews",
                        color = White
                    )
                }

            }
        }
    }
}

@Composable
fun DoctorList(doctors: List<Doctor>, navController: NavController) {
    var selectedDoctorId: String? by remember { mutableStateOf(null) }
    var selectedDoctor: Doctor? by
    remember { mutableStateOf(null) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (doctors.isEmpty()) {
            Text("No doctors available")
        } else
            LazyColumn {
                item { Text("Select a doctor:", color = White) }
                items(doctors) { doctor ->
                    SingleDoctor(
                        doctor = doctor,
                        isSelected = (selectedDoctorId == doctor.id),
                        onDoctorSelected = { id -> selectedDoctorId = id },
                        navController = navController
                    )
                }
            }
    }

}

class MainViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    val searchText = _searchText.asStateFlow()
    var doctors: StateFlow<List<Doctor>>? = MutableStateFlow(emptyList())

    val isSearching = _isSearching.asStateFlow()

    init {
        viewModelScope.launch {
            val _doctors = MutableStateFlow(getDoctorList())

            doctors = searchText
                .debounce(500L)
                .onEach { _isSearching.update { true } }
                .combine(_doctors) { text, doctors ->
                    if (text.isBlank()) {
                        doctors
                    } else {
                        doctors.filter {
                            it.doesMatchSearchQuery(text)
                        }
                    }
                }
                .onEach { _isSearching.update { false } }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    _doctors.value
                )
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

}

suspend fun getDoctorList(): List<Doctor> {
    val mFireBase = DoctorDAO()
    val doctors = mFireBase.getAllDoctors()
    return doctors
}

@Composable
fun DoctorScreen(navController: NavController) {
    val viewModel = viewModel<MainViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val person by viewModel.doctors!!.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    UserProvider { profilePictureUrl ->
        ModelNavDrawerUser(navController, drawerState, profilePictureUrl) {
            Column(modifier = Modifier.padding(16.dp)) {
                SearchBar(modifier = Modifier.fillMaxWidth(), viewModel = viewModel, searchText)
                Spacer(modifier = Modifier.height(16.dp))
                MediMateButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { navController.navigate(Screen.MainUser.route) },
                    text = "Go back"
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    DoctorList(doctors = person, navController = navController)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
    searchText: String
) {
    TextField(
        value = searchText, onValueChange = viewModel::onSearchTextChange, trailingIcon = {
        Icon(Icons.Default.Search, contentDescription = null)
    }, placeholder = { Text(stringResource(id = R.string.place_holder_search)) },
        modifier = modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )

}


@Preview(showSystemUi = true)
@Composable
fun DoctorsViewPreview() {
    MediMateTheme {
        DoctorScreen(navController = rememberNavController())
    }
    //dto,, przekazac tylko dane wyswietlane
}
