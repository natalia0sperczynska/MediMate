package com.example.medimate.admin.usersManagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.admin.ModelNavDrawerAdmin
import com.example.medimate.admin.usersManagement.usersView.MainUserViewModel
import com.example.medimate.admin.usersManagement.usersView.SearchUserBar
import com.example.medimate.admin.usersManagement.usersView.UserList
import com.example.medimate.firebase.admin.AdminDAO
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.user.User
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.user.doctorsView.DoctorList
import com.example.medimate.user.doctorsView.MainViewModel
import com.example.medimate.user.doctorsView.SearchBar

@Composable
fun ManageUsers(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val viewModel = viewModel<MainUserViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val users by viewModel.users.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    ModelNavDrawerAdmin(navController, drawerState, scope) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                SearchUserBar(
                    modifier = Modifier.fillMaxWidth(),
                    viewModel = viewModel,
                    searchText = searchText
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Manage Users", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))

                if (isSearching) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    UserList(users = users, navController = navController)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageUsersPreview() {
    MediMateTheme {
        ManageUsers(navController = rememberNavController())
    }
}