package com.example.medimate.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medimate.appointments.AppointmentsScreen
import com.example.medimate.login.LoginScreen
import com.example.medimate.mainViews.admin.MainAdminScreen
import com.example.medimate.mainViews.doctor.MainDoctorScreen
import com.example.medimate.mainViews.MainScreen
import com.example.medimate.mainViews.user.MainUserScreen
import com.example.medimate.register.RegisterScreen
import com.example.medimate.tests.getSampleDoctors
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
    object AppointmentsDoctor : Screen("appointments_doctor/{doctorId}"){
        fun createRoute(doctorId: String) = "appointments_doctor/$doctorId"
    }
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
        composable(route = Screen.AppointmentsDoctor.route,
            arguments = listOf(navArgument("doctorId"){type = NavType.StringType})) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            //val doctor = getDoctorById(doctorId)
            val doctor = getSampleDoctors().find { it.id == doctorId }
            AppointmentsScreen(
                navController = navController,
                selectedDoctor = doctor
            )
        }
        composable(Screen.Doctors.route) {
            DoctorScreen(navController)

        }
    }

}
