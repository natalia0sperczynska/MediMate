package com.example.medimate.firebase.appointment

import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.user.appointments.getAvailableTermsForDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.tasks.await

/**
 * Class for interacting with Firebase Firestore for appointment data management.
 */
class AppointmentDAO {
    val mFireStore = FirebaseFirestore.getInstance()
    val doctorDAO = DoctorDAO()
    suspend fun addAppointment(appointment: Appointment) {
        val firestore = FirebaseFirestore.getInstance()
        //transaction
        try {
            val doctor =
                doctorDAO.getDoctorById(appointment.doctorId) ?: throw Exception("Doctor not found")

            if (!isTimeSlotAvailable(doctor, appointment.date, appointment.time)) {
                throw Exception("Time slot is no longer available")
            }

            mFireStore.runTransaction { transaction ->
                val appointmentRef = mFireStore.collection("appointments")
                    .document(appointment.id)
                transaction.set(appointmentRef, appointment.toMap())

                updateDoctorAvailability(transaction, doctor, appointment.date, appointment.time)
            }.await()
        } catch (e: Exception) {
            throw Exception("Error saving appointment data: ${e.message}")
        }
    }
    private suspend fun isTimeSlotAvailable(
        doctor: Doctor,
        date: String,
        time: String
    ): Boolean {
        val terms = getAvailableTermsForDate(doctor, date)
        return terms.any { term ->
            "${term.startTime}-${term.endTime}" == time && term.isAvailable
        }
    }

    private fun updateDoctorAvailability(
        transaction: Transaction,
        doctor: Doctor,
        date: String,
        time: String
    ) {
        val doctorRef = mFireStore.collection("doctors").document(doctor.id)
        val updatedChanges = doctor.availabilityChanges.toMutableMap()

        val currentTerms = updatedChanges[date] ?:
        getAvailableTermsForDate(doctor, date).map { it.copy() }

        val updatedTerms = currentTerms.map { term ->
            if ("${term.startTime}-${term.endTime}" == time) {
                term.copy(isAvailable = false)
            } else {
                term
            }
        }

        updatedChanges[date] = updatedTerms
        transaction.update(doctorRef, "availabilityChanges", updatedChanges)
    }

    suspend fun deleteAppointment(appointmentId: String) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("appointments").document(appointmentId).delete().await()
        } catch (e: Exception) {
            throw Exception("Error deleting appointment data: ${e.message}")
        }
    }

    suspend fun getAppointments(): List<Appointment>? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val mFireBase = UserDAO()
        return if (userId != null) mFireBase.loadAppointments(userId) else emptyList()
    }

    suspend fun getPastAppointments(): List<Appointment>? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val mFireBase = UserDAO()
        return if (userId != null) mFireBase.loadPastAppointments(userId) else emptyList()
    }

    suspend fun getFutureAppointments(): List<Appointment>? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val mFireBase = UserDAO()
        return if (userId != null) mFireBase.loadFutureAppointments(userId) else emptyList()
    }

    suspend fun updateAppointment(appointmentId: String, updatedData: Map<String, Any?>) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            val filteredData =
                updatedData.filterValues { it != null && !(it is String && it.isBlank()) }
            if (filteredData.isEmpty()) return
            mFireStore.collection("appointments")
                .document(appointmentId)
                .update(filteredData)
                .await()
        } catch (e: Exception) {
            throw Exception("Error updating appointment data: ${e.message}")
        }
    }


////simuntanious update przyklad z docs
//private fun writeNewPost(userId: String, username: String, title: String, body: String) {
//    // Create new post at /user-posts/$userid/$postid and at
//    // /posts/$postid simultaneously
//    val key = database.child("posts").push().key
//    if (key == null) {
//        Log.w(TAG, "Couldn't get push key for posts")
//        return
//    }
//
//    val post = Post(userId, username, title, body)
//    val postValues = post.toMap()
//
//    val childUpdates = hashMapOf<String, Any>(
//        "/posts/$key" to postValues,
//        "/user-posts/$userId/$key" to postValues,
//    )
//
//    database.updateChildren(childUpdates)
}

