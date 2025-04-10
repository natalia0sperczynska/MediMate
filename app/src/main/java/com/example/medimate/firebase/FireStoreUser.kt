package com.example.medimate.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Class for interacting with Firebase Firestore for user data management.
 */
class FireStoreUser {
    private val mFireStore = FirebaseFirestore.getInstance()//w kazdej metodzie

    /**
     * Function registers or updates a user in the Firestore database.
     * If a user with the same ID already exists, their document will be overwritten.
     *
     * @param user The user object to register or update.
     * @throws Exception If an error occurs while saving the user data.
     */
    suspend fun registerOrUpdateUser(user: User) {
        try {
            mFireStore.collection("users").document(user.id).set(user).await()
        } catch (e: Exception) {
            throw Exception("Error saving user data: ${e.message}")
        }
    }

    /**
     * Function registers or updates a doctors in the Firestore database.
     * If a doctor with the same ID already exists, their document will be overwritten.
     *
     * @param doctor The doctor object to register or update.
     * @throws Exception If an error occurs while saving the doctors data.
     */
    suspend fun registerOrUpdateDoctor(doctor: Doctor) {
        try {
            mFireStore.collection("doctors").document(doctor.id).set(doctor).await()
        } catch (e: Exception) {
            throw Exception("Error saving user data: ${e.message}")
        }
    }

    /**
     * Function registers or updates an admin in the Firestore database.
     * If an admin with the same ID already exists, their document will be overwritten.
     *
     * @param admin The admin object to register or update.
     * @throws Exception If an error occurs while saving the doctors data.
     */
    suspend fun registerOrUpdateAdmin(admin: Admin) {
        try {
            mFireStore.collection("admins").document(admin.id).set(admin).await()
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
     * Function loads doctors data from Firestore based on the given user ID.
     *
     * @param doctorId The ID of the doctor to fetch.
     * @return A map containing the doctor data, or null if the document does not exist.
     * @throws Exception If an error occurs while loading the doctor data.
     */
    suspend fun loadDoctorData(doctorId: String): Map<String, Any>? {
        try {

            val documentSnapshot = mFireStore.collection("doctors")
                .document(doctorId)
                .get()
                .await()
            return documentSnapshot.data
        } catch (e: Exception) {

            throw Exception("Error loading doctor data: ${e.message}")
        }
    }

    /**
     * Function loads admin data from Firestore based on the given user ID.
     *
     * @param adminId The ID of the admin to fetch.
     * @return A map containing the admin data, or null if the document does not exist.
     * @throws Exception If an error occurs while loading the admin data.
     */
    suspend fun loadAdminData(adminId: String): Map<String, Any>? {
        try {

            val documentSnapshot = mFireStore.collection("admins")
                .document(adminId)
                .get()
                .await()
            return documentSnapshot.data
        } catch (e: Exception) {

            throw Exception("Error loading admin data: ${e.message}")
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

    /**
     * Function updates doctors data in Firestore.
     * Only non-null and non-blank values are updated in the user's document.
     *
     * @param doctorId The ID of the doctor to update.
     * @param updatedData A map containing the updated data for the doctor.
     * @throws Exception If an error occurs while updating the doctor data.
     */
    suspend fun updateDoctorData(doctorId: String, updatedData: Map<String, Any?>) {
        try {

            val filteredData = updatedData.filterValues { value ->
                value != null && !(value is String && value.isBlank())
            }

            if (filteredData.isEmpty()) return

            mFireStore.collection("doctors")
                .document(doctorId)
                .update(filteredData)
                .await()

        } catch (e: Exception) {
            throw Exception("Error updating user data: ${e.message}")
        }
    }

    /**
     * Function updates admin data in Firestore.
     * Only non-null and non-blank values are updated in the admins document.
     *
     * @param adminId The ID of the admin to update.
     * @param updatedData A map containing the updated data for the admin.
     * @throws Exception If an error occurs while updating the admin data.
     */
    suspend fun updateAdminData(adminId: String, updatedData: Map<String, Any?>) {
        try {

            val filteredData = updatedData.filterValues { value ->
                value != null && !(value is String && value.isBlank())
            }

            if (filteredData.isEmpty()) return


            mFireStore.collection("admins")
                .document(adminId)
                .update(filteredData)
                .await()

        } catch (e: Exception) {
            throw Exception("Error updating admin data: ${e.message}")
        }
    }

    suspend fun checkIfUserExists(email: String): Boolean {
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

suspend fun getAllDoctors(): List<Doctor> {
        val doctorsList = mutableListOf<Doctor>()
        val result = mFireStore.collection("doctors").get().await()
        for (document in result) {
            val doctor = document.toObject(Doctor::class.java)
            doctorsList.add(doctor)
        }
        return doctorsList
    }
}