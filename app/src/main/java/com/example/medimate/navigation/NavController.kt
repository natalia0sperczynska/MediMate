package com.example.medimate.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medimate.appointments.AppointmentsScreen
import com.example.medimate.login.LoginScreen
import com.example.medimate.mainViews.admin.MainAdminScreen
import com.example.medimate.mainViews.doctor.MainDoctorScreen
import com.example.medimate.mainViews.MainScreen
import com.example.medimate.mainViews.user.MainUserScreen
import com.example.medimate.register.RegisterScreen
import com.example.medimate.user.UpdateDataScreen
import com.example.medimate.user.DoctorScreen

//import com.example.medimate.auth.LoginScreen
//import com.example.medimate.auth.RegisterScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object MainDoctor : Screen("main_doctor")
    object MainAdmin : Screen("main_admin")
    object MainUser : Screen("main_user")
    object Login : Screen("login")
    object Register : Screen("register")
    object UpdateData : Screen("update_data")
    object Appointments : Screen("appointments")
    object Doctors : Screen("doctors")
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.UpdateData.route) {
            UpdateDataScreen(navController)
        }
        composable(Screen.MainDoctor.route) {
            MainDoctorScreen(navController)
        }
        composable(Screen.MainAdmin.route) {
            MainAdminScreen(navController)
        }
        composable(Screen.MainUser.route) {
            MainUserScreen(navController)
        }
        composable(Screen.Appointments.route) {
            AppointmentsScreen(navController)
        }
        composable(Screen.Doctors.route) {
            DoctorScreen(navController)

        }
    }

}
