package com.example.medimate.firebase.admin

import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.user.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Class for interacting with Firebase Firestore for user data management.
 */
class AdminDAO {
    /**
     * Function registers or updates an admin in the Firestore database.
     * If an admin with the same ID already exists, their document will be overwritten.
     *
     * @param admin The admin object to register or update.
     * @throws Exception If an error occurs while saving the doctors data.
     */
    suspend fun registerOrUpdateAdmin(admin: Admin) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("admins").document(admin.id).set(admin).await()
        } catch (e: Exception) {
            throw Exception("Error saving user data: ${e.message}")
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
        val mFireStore = FirebaseFirestore.getInstance()
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
     * Function updates admin data in Firestore.
     * Only non-null and non-blank values are updated in the admins document.
     *
     * @param adminId The ID of the admin to update.
     * @param updatedData A map containing the updated data for the admin.
     * @throws Exception If an error occurs while updating the admin data.
     */
    suspend fun updateAdminData(adminId: String, updatedData: Map<String, Any?>) {
        val mFireStore = FirebaseFirestore.getInstance()
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
    suspend fun addDoctor(adminId: String, doctor: Doctor) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("doctors").document(doctor.id).set(doctor).await()
        } catch (e: Exception) {
            throw Exception("Error adding doctor: ${e.message}")
        }
    }
    suspend fun deleteDoctor(adminId: String, doctor: Doctor) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("doctors").document(doctor.id).delete().await()
        } catch (e: Exception) {
            throw Exception("Error deleting doctor: ${e.message}")
        }
    }
    suspend fun addUser(adminId: String, user: User) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("users").document(user.id).set(user).await()
        } catch (e: Exception) {
            throw Exception("Error adding user: ${e.message}")
        }
    }
    suspend fun deleteUser(adminId: String, user: User) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("users").document(user.id).delete().await()
        } catch (e: Exception) {
            throw Exception("Error deleting user: ${e.message}")
        }
    }
    suspend fun getAllAdmins(): List<Admin> {
        val mFireStore = FirebaseFirestore.getInstance()
        return mFireStore.collection("admins").get().await().toObjects(Admin::class.java)
    }
    suspend fun getAllDoctors(): List<Doctor> {
        val mFireStore = FirebaseFirestore.getInstance()
        return mFireStore.collection("doctors").get().await().toObjects(Doctor::class.java)
    }
    fun generateDoctorId(): String {
        return FirebaseFirestore.getInstance().collection("doctors").document().id
    }

}
