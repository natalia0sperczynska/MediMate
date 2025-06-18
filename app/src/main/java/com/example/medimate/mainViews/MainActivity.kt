package com.example.medimate.mainViews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.medimate.navigation.AppNavHost
import com.example.medimate.ui.theme.MediMateTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediMateTheme {
                    MediMateApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
@Composable
fun MediMateApp(modifier: Modifier){
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}




