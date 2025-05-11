package com.example.medimate.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Class for interacting with Firebase Firestore for user data management.
 */
class UserDAO {

    /**
     * Function registers or updates a user in the Firestore database.
     * If a user with the same ID already exists, their document will be overwritten.
     *
     * @param user The user object to register or update.
     * @throws Exception If an error occurs while saving the user data.
     */
    suspend fun registerOrUpdateUser(user: User) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("users").document(user.id).set(user).await()
        } catch (e: Exception) {
            throw Exception("Error saving user data: ${e.message}")
        }
    }


    /**
     * Function loads user data from Firestore based on the given user ID.
     *
     * @param userId The ID of the user to fetch.
     * @return A map containing the user data, or null if the document does not exist.
     * @throws Exception If an error occurs while loading the user data.
     */
    suspend fun loadUserData(userId: String): Map<String, Any>? {
        val mFireStore = FirebaseFirestore.getInstance()
        try {

            val documentSnapshot = mFireStore.collection("users")
                .document(userId)
                .get()
                .await()
            return documentSnapshot.data
        } catch (e: Exception) {

            throw Exception("Error loading user data: ${e.message}")
        }
    }


    /**
     * Function updates user data in Firestore.
     * Only non-null and non-blank values are updated in the user's document.
     *
     * @param userId The ID of the user to update.
     * @param updatedData A map containing the updated data for the user.
     * @throws Exception If an error occurs while updating the user data.
     */
    suspend fun updateUserData(userId: String, updatedData: Map<String, Any?>) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {

            val filteredData = updatedData.filterValues { value ->
                value != null && !(value is String && value.isBlank())
            }

            if (filteredData.isEmpty()) return


            mFireStore.collection("users")
                .document(userId)
                .update(filteredData)
                .await()

        } catch (e: Exception) {
            throw Exception("Error updating user data: ${e.message}")
        }
    }

    suspend fun checkIfUserExists(email: String): Boolean {
        val mFireStore = FirebaseFirestore.getInstance()
        return try {
            val querySnapshot = mFireStore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            throw Exception("Error checking user existence: ${e.message}")
        }
    }
    suspend fun loadAppointments(userid: String): List<Appointment> {
        val mFireStore = FirebaseFirestore.getInstance()
        val appointmentsList = mutableListOf<Appointment>()
        val result = mFireStore.collection("appointments").get().await()
        for (document in result) {
            val appointment = document.toObject(Appointment::class.java)
            if (appointment.patientId== userid)
                appointmentsList.add(appointment)
        }
        return appointmentsList

    }
}