package com.example.myfirstapp.firebase
/**
 * Data class representing a Doctor.
 *
 * @property id Unique identifier for the doctor.
 * @property name The doctor's first name.
 * @property surname The doctor's last name.
 * @property email The doctor's email address.
 * @property phoneNumber The doctor's phone number.
 * @property profilePicture URL to the doctor's profile picture.
 */
data class Doctor(
    val id: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profilePicture: String = ""
)

