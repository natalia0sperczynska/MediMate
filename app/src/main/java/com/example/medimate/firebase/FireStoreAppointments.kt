package com.example.medimate.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Class for interacting with Firebase Firestore for appointment data management.
 */
class FireStoreAppointments {

    suspend fun addAppointment(appointment: Appointment) {
        //transaction
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("appointments").document(appointment.id).set(appointment).await()
        } catch (e: Exception) {
            throw Exception("Error saving appointment data: ${e.message}")
        }

    }
    suspend fun deleteAppointment(appointmentId: String) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("appointments").document(appointmentId).delete().await()
        } catch (e: Exception) {
            throw Exception("Error deleting appointment data: ${e.message}")
    }
    }

    suspend fun updateAppointment(appointmentId: String, updatedData: Map<String, Any?>) {

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
