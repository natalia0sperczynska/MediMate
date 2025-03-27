package com.example.medimate.mainViews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.medimate.navigation.AppNavHost
import com.example.medimate.ui.theme.MediMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediMateApp()
        }
    }
}
@Composable
fun MediMateApp(){
    MediMateTheme {
        val navController = rememberNavController()
        AppNavHost(navController = navController)

    }
}



