package com.example.medimate.updateData
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.medimate.R
import com.example.medimate.firebase.FireStore
import com.example.medimate.firebase.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Activity to update user data in the Firestore database.
 */
class UpdateDataActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var surnameInput:EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var allergiesInput: EditText
    private lateinit var diseasesInput: EditText
    private lateinit var medicationsInput: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button
    private lateinit var profileImageView: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val firestoreClass = FireStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_data)

        initializeUI()

        val userId = auth.currentUser?.uid

        if (userId != null) {
            lifecycleScope.launch {
                try {
                    val data = firestoreClass.loadUserData(userId)
                    if (data != null) {
                        val user = User.fromMap(data)
                        populateUI(user)
                    } else {
                        Toast.makeText(this@UpdateDataActivity, "No user data found.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@UpdateDataActivity, "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        submitButton.setOnClickListener {
            if (userId != null) {
                lifecycleScope.launch {
                    updateUserData(userId)
                }
            } else {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Initialize UI components by finding their views from the layout.
     */
    private fun initializeUI() {
        nameInput = findViewById(R.id.nameInput)
        surnameInput=findViewById(R.id.surnameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        addressInput = findViewById(R.id.addressInput)
        allergiesInput = findViewById(R.id.allergiesInput)
        diseasesInput = findViewById(R.id.diseasesInput)
        medicationsInput = findViewById(R.id.medicationsInput)
        submitButton = findViewById(R.id.submitButton)
        cancelButton = findViewById(R.id.cancelButton)
        profileImageView = findViewById(R.id.profileImageView)
    }

    /**
     * Populate the UI with user data.
     *
     * @param user The User object containing the data to display.
     */
    private fun populateUI(user: User) {
        nameInput.setText(user.name ?: "")
        surnameInput.setText(user.surname ?: "")
        emailInput.setText(user.email)
        phoneInput.setText(user.phoneNumber)

        val address = user.address.values.joinToString(", ")
        addressInput.setText(address)

        allergiesInput.setText(user.allergies.joinToString(", "))
        diseasesInput.setText(user.diseases.joinToString(", "))
        medicationsInput.setText(user.medications.joinToString(", "))

        if (user.profilePictureUrl.isNotEmpty()) {
            Glide.with(this)
                .load(Uri.parse(user.profilePictureUrl))
                .placeholder(R.drawable.profile_pic)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.profile_pic)
        }
    }

    /**
     * Collect updated data from UI and update it in Firestore.
     *
     * @param userId The ID of the user being updated.
     */
    private suspend fun updateUserData(userId: String) {
        val addressParts = addressInput.text.toString().split(",").map { it.trim() }
        val addressMap = if (addressParts.size == 3) {
            mapOf(
                "city" to addressParts[0],
                "street" to addressParts[1],
                "postcode" to addressParts[2]
            )
        } else {
            mapOf()
        }

        val updatedData = mapOf(
            "name" to nameInput.text.toString(),
            "surname" to surnameInput.text.toString(),
            "email" to emailInput.text.toString(),
            "phoneNumber" to phoneInput.text.toString(),
            "address" to addressMap,
            "allergies" to allergiesInput.text.toString().split(",").map { it.trim() },
            "diseases" to diseasesInput.text.toString().split(",").map { it.trim() },
            "medications" to medicationsInput.text.toString().split(",").map { it.trim() }
        )

        try {
            firestoreClass.updateUserData(userId, updatedData)
            Toast.makeText(this, "Data updated successfully!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
