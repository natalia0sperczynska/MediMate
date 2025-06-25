package com.example.medimate.mainViews

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.medimate.navigation.AppNavHost
import com.example.medimate.ui.theme.MediMateTheme
// import com.example.medimate.firebase.appointment.AppointmentWorkScheduler
// import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : FragmentActivity() {

    // Notification permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // You can optionally handle UI feedback here
        if (!isGranted) {
            Toast.makeText(this, "Notifications permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for notification permission (Android 13+)
        askNotificationPermission()

        // Create channel for push notifications
        createNotificationChannel()

        // Optional: Schedule background appointment check
        // AppointmentWorkScheduler.scheduleAppointmentCheck(this)

        // Optional: Firebase Messaging Token (for push notifications)
        /*
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM Token", token)
            Toast.makeText(this, "FCM Token: $token", Toast.LENGTH_SHORT).show()
        }
        */

        // Set Composable content
        setContent {
            MediMateTheme {
                MediMateApp(modifier = Modifier.fillMaxSize())
            }
        }
    }

    /**
     * Request notification permission if needed (Android 13+)
     */
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /**
     * Create a default notification channel (Android 8+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel_id",
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}

/**
 * Root composable for the app
 */
@Composable
fun MediMateApp(modifier: Modifier) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}
