package com.example.medimate

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object MainUser : Screen("main_user_screen")
    object MainDoctor : Screen("main_doctor_screen")
    object MainAdmin : Screen("main_admin_screen")
    object Register : Screen("register_screen")
}
