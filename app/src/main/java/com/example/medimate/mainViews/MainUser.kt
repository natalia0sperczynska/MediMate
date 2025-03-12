package com.example.medimate.mainViews

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
//import com.example.medimate.maps.MapsActivity
import com.example.medimate.R
//import com.example.myfirstapp.notifications.SetNotificationActivity
//import com.example.myfirstapp.doctorsView.DoctorsRecyclerView
import com.example.medimate.firebase.FireStore
import com.example.medimate.updateData.UpdateDataActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
/**
 * Main activity for a logged-in user.
 * Displays user-related information and provides options to view doctors, update data, calculate BMI, and more.
 */
class MainUser : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestoreClass = FireStore()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user)

       val userId=auth.currentUser?.uid

        val welcomeText: TextView = findViewById(R.id.nameAndSurname)

       findViewById<TextView>(R.id.message).text = "Welcome!"

       if (userId != null) {
           loadUserData(userId,welcomeText)
       }

        val logout: LinearLayout = findViewById(R.id.logout)
       logout.setOnClickListener {
           FirebaseAuth.getInstance().signOut() // Sign out the user
           val intent = Intent(this, MainActivity::class.java)
           startActivity(intent)
           finish()
       }
        val updateData: LinearLayout = findViewById(R.id.updateData)


      updateData.setOnClickListener {
           val intent = Intent(this, UpdateDataActivity::class.java)
           startActivity(intent)
       }

//        val maps: LinearLayout = findViewById(R.id.maps)
//
//
//        maps.setOnClickListener {
//            val intent = Intent(this, MapsActivity::class.java)
//            startActivity(intent)
//        }
//        val doctors: LinearLayout = findViewById(R.id.doctors)
//
//
//        doctors.setOnClickListener {
//            val intent = Intent(this, DoctorsRecyclerView::class.java)
//            startActivity(intent)
//        }


//        val notification: LinearLayout = findViewById(R.id.Notifications)
//
//
//        notification.setOnClickListener {
//            val intent = Intent(this, SetNotificationActivity::class.java)
//            startActivity(intent)
//        }

   }
    /**
     * Loads user data from Firestore and displays it in the UI.
     *
     * @param userId The user's unique ID.
     * @param welcomeText The TextView to display the welcome message.
     */
    private fun loadUserData(userId: String,welcomeText: TextView) {
        lifecycleScope.launch {
            try {
                val data = firestoreClass.loadUserData(userId)
                if (data != null) {
                    val userName=data.getValue("name")
                    welcomeText.text = "Good to see you again $userName!"
                } else {
                    Toast.makeText(this@MainUser, "No user data found.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainUser,
                    "Failed to load user data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}