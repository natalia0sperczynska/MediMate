package com.example.medimate.admin.doctorsManagement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medimate.admin.ModelNavDrawerAdmin
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.user.doctorsView.DoctorList
import com.example.medimate.user.doctorsView.MainViewModel
import com.example.medimate.user.doctorsView.SearchBar

@Composable
fun DoctorsAdmin(navController: NavController) {
    val viewModel = viewModel<MainViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val person by viewModel.doctors!!.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModelNavDrawerAdmin(navController,drawerState,scope= rememberCoroutineScope()) {
        Column(modifier = Modifier.padding(16.dp)) {
            SearchBar(modifier = Modifier.fillMaxWidth(), viewModel = viewModel, searchText)
            Spacer(modifier = Modifier.height(16.dp))
            MediMateButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { navController.navigate(Screen.MainAdmin.route)},
                text="Go back")
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