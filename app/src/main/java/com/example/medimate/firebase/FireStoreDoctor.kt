package com.example.medimate.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
/**
 * Class for interacting with Firebase Firestore for user data management.
 */
class FireStoreDoctor {
    /**
     * Function registers or updates a user in the Firestore database.
     * If a user with the same ID already exists, their document will be overwritten.
     *
     * @param user The user object to register or update.
     * @throws Exception If an error occurs while saving the user data.
     */

    suspend fun registerOrUpdateDoctor(doctor: Doctor) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("doctors").document(doctor.id).set(doctor).await()
        } catch (e: Exception) {
            throw Exception("Error saving user data: ${e.message}")
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
        val mFireStore = FirebaseFirestore.getInstance()
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
     * Function updates doctors data in Firestore.
     * Only non-null and non-blank values are updated in the user's document.
     *
     * @param doctorId The ID of the doctor to update.
     * @param updatedData A map containing the updated data for the doctor.
     * @throws Exception If an error occurs while updating the doctor data.
     */
    suspend fun updateDoctorData(doctorId: String, updatedData: Map<String, Any?>) {
        val mFireStore = FirebaseFirestore.getInstance()
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

    suspend fun getAllDoctors(): List<Doctor> {
        val mFireStore = FirebaseFirestore.getInstance()
        val doctorsList = mutableListOf<Doctor>()
        val result = mFireStore.collection("doctors").get().await()
        for (document in result) {
            val doctor = document.toObject(Doctor::class.java)
            doctorsList.add(doctor)
        }
        return doctorsList
    }
    suspend fun loadAppointments(doctorId: String): List<Appointment> {
        val mFireStore = FirebaseFirestore.getInstance()
        val appointmentsList = mutableListOf<Appointment>()
        val result = mFireStore.collection("appointments").get().await()
        for (document in result) {
            val appointment = document.toObject(Appointment::class.java)
            if (appointment.doctor?.id == doctorId)
                appointmentsList.add(appointment)
        }
        return appointmentsList

    }


}


