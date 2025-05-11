package com.example.medimate.admin.usersManagement

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.admin.ModelNavDrawerAdmin
import com.example.medimate.ui.theme.MediMateTheme

@Composable
fun ManageUsers(navController: NavController){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModelNavDrawerAdmin(navController,drawerState,scope) {

    }

}

@Preview(showBackground = true)
@Composable
fun ManageUsersPreview() {
    MediMateTheme {
        ManageUsers(navController = rememberNavController())
    }
}