package com.example.medimate.navigation
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medimate.appointments.AppointmentsScreen
import com.example.medimate.chat.ChatScreen
import com.example.medimate.user.appointments.AppointmentsScreen
import com.example.medimate.login.LoginScreen
import com.example.medimate.admin.main.MainAdminScreen
import com.example.medimate.doctor.main.MainDoctorScreen
import com.example.medimate.mainViews.MainScreen
import com.example.medimate.mainViews.user.MainUserScreen
import com.example.medimate.register.RegisterScreen
import com.example.medimate.user.appointments.HistoryAppointmentsScreen
import com.example.medimate.user.appointments.SingleAppointment
import com.example.medimate.user.appointments.YourFutureAppointmentsScreen
import com.example.medimate.user.updateData.UpdateDataScreen
import com.example.medimate.user.doctorsView.DoctorScreen

import com.example.medimate.tests.getSampleDoctors
import com.example.medimate.user.UpdateDataScreen
import com.example.medimate.user.DoctorScreen
import com.example.medimate.chat.ChatScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object MainDoctor : Screen("main_doctor")
    object MainAdmin : Screen("main_admin")
    object AddDoctor : Screen("add_doctor")
    object ManageUsers : Screen("manage_users")
    object SingleAppointment : Screen("single_appointment/{appointmentId}"){
        fun createRoute(appointmentId:String) ="single_appointment/$appointmentId"
    }
    object MainUser : Screen("main_user")
    object Login : Screen("login")
    object Register : Screen("register")
    object UpdateData : Screen("update_data")
    object Appointments : Screen("appointments")
    object AppointmentsDoctor : Screen("appointments_doctor/{doctorId}"){
        fun createRoute(doctorId: String) = "appointments_doctor/$doctorId"
    }
    object Doctors : Screen("doctors")
    object ChatScreen : Screen("chat/{targetUserId}") {
        fun createRoute(targetUserId: String) = "chat/$targetUserId"
    }
    object DoctorsAdmin : Screen("doctors_admin")
    object FutureAppointments : Screen("future_appointments")
    object AppointmentsHistory : Screen("past_appointments")
    object UserDocumentation : Screen("user_documentation/{userId}"){
        fun createRoute(userId:String) = "user_documentation/$userId"
    }
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
        composable(Screen.MainUser.route) {
            MainUserScreen(navController)
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
        composable(Screen.AddDoctor.route) {
            AddDoctor(navController)
        }
        composable(Screen.DoctorsAdmin.route) {
            DoctorsAdmin(navController)
        }
        composable(Screen.ManageUsers.route) {
            ManageUsers(navController)
        }
        composable(Screen.Doctors.route) {
           DoctorScreen(navController)
        }
        composable(Screen.Appointments.route) {
            AppointmentsScreen(navController)
        }
        composable(Screen.FutureAppointments.route) {
            YourFutureAppointmentsScreen(navController)
        }
        composable(Screen.AppointmentsHistory.route) {
            HistoryAppointmentsScreen(navController)
        }

        composable(
            route = Screen.UserDocumentation.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserDocumentation(navController, userId)
            }
        }
        composable(
            route = Screen.SingleAppointment.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")
                ?: ""
            SingleAppointment(appointmentId, navController)
        }
        composable(
            route = Screen.AppointmentsDoctor.route,
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            val viewModel = viewModel<AppointmentsModel>()

            LaunchedEffect(doctorId) {
                if (doctorId != null) {
                    viewModel.loadDoctorById(doctorId)
                }
            }

            AppointmentsScreen(
                navController = navController,
                selectedDoctorId = doctorId
            )
        }
        composable(Screen.Doctors.route) {
            DoctorScreen(navController)

        }
        composable(route = Screen.ChatScreen.route,
            arguments = listOf(navArgument("targetUserId") { type = NavType.StringType })
        ) { backStackEntry ->
            val targetUserId = backStackEntry.arguments?.getString("targetUserId") ?: return@composable
            ChatScreen(targetUserId = targetUserId)
        }
    }
}
