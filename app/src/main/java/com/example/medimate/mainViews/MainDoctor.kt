package com.example.medimate.mainViews

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.medimate.R
import com.example.medimate.firebase.FireStore
import com.example.medimate.updateData.UpdateDataActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainDoctor : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val firestoreClass = FireStore()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_doctor)

        val doctorId=auth.currentUser?.uid

        val welcomeText: TextView = findViewById(R.id.nameAndSurname)

        findViewById<TextView>(R.id.message).text = "Welcome doctor!"

        if (doctorId != null) {
            loadDoctorData(doctorId,welcomeText)
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
    }
    /**
     * Loads user data from Firestore and displays it in the UI.
     *
     * @param doctorId The doctors's unique ID.
     * @param welcomeText The TextView to display the welcome message.
     */
    private fun loadDoctorData(doctorId: String,welcomeText: TextView) {
        lifecycleScope.launch {
            try {
                val data = firestoreClass.loadDoctorData(doctorId)
                if (data != null) {
                    val userName=data.getValue("name")
                    welcomeText.text = "Good to see you again $userName!"
                } else {
                    Toast.makeText(this@MainDoctor, "No user data found.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainDoctor,
                    "Failed to load user data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}