package com.example.medimate.notifications

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.healme.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Called if the FCM registration token is updated. This may occur if the security of
 * the previous token had been compromised. Note that this is called when the
 * FCM registration token is initially generated so this is where you would retrieve the token.
 */

class MyFirebaseMessagingService : FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            Firebase.firestore.collection("users").document(userId)
                .update("fcmToken", token)
                .addOnFailureListener { e ->
                    Log.e("FCM", "Eroor token save", e)
                    Firebase.firestore.collection("doctors").document(userId)
                        .update("fcmToken", token)
                        .addOnFailureListener { e2 ->
                            Log.e("FCM", "Doctor token save also failed", e2)
                        }
                }
        }

    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        if (title != null && body != null) {
            val builder = NotificationCompat.Builder(this, "default_channel_id")
                .setSmallIcon(R.drawable.profile_pic)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(123, builder.build())
        }
    }

}