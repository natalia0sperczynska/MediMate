package com.example.medimate.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Class for interacting with Firebase Firestore for user data management.
 */
class FireStoreAdmin {
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

}