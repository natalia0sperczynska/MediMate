package com.example.medimate.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.ui.theme.MediMateTheme

@Composable
fun ManageUsers(navController: NavController){

}

@Preview(showBackground = true)
@Composable
fun ManageUsersPreview() {
    MediMateTheme {
        ManageUsers(navController = rememberNavController())
    }
}