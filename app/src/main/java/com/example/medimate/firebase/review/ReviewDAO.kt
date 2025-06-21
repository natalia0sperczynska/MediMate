package com.example.medimate.firebase.review

import android.util.Log
import com.example.medimate.firebase.doctor.Doctor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import java.util.Date

class ReviewDAO {
    private val firestore = FirebaseFirestore.getInstance()
    private val doctorsCollection = firestore.collection("doctors")

    suspend fun AddReview(doctorId:String, review: Review) {
            val doctorRef = doctorsCollection.document(doctorId)
            val doctor = doctorRef.get().await().toObject(Doctor::class.java) ?: return

            val updatedReviews = doctor.reviews.toMutableList().apply {
                add(review)
            }
            val newRating = updatedReviews.map { it.rate }.average()
            doctorRef.update(
                mapOf(
                    "reviews" to updatedReviews,
                    "rating" to newRating
                )
            ).await()

        }
    suspend fun getReviewsForDoctor(doctorId: String): List<Review> {
        return try {
            val doctorSnapshot = doctorsCollection.document(doctorId).get().await()
            if (doctorSnapshot.exists()) {
                val reviewsData = doctorSnapshot.get("reviews") as? List<Map<String, Any>> ?: emptyList()
                reviewsData.map { data ->
                    Review(
                        rate = data["rate"] as? Double ?: 0.0,
                        text = data["text"] as? String ?: "",
                        userId = data["userId"] as? String ?: "",
                        doctorId = data["doctorId"] as? String ?: "",
                        timestamp = when (val ts = data["timestamp"]) {
                            is Long -> ts
                            is Timestamp -> ts.toDate().time
                            else -> System.currentTimeMillis()
                        }
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ReviewDAO", "Error getting reviews: ${e.message}")
            emptyList()
        }
    }
    suspend fun deleteReview(doctorId:String, review:Review, currentReviews:List<Review>, newRating:Double){
        val doctorRef = doctorsCollection.document(doctorId)
        try {
            doctorRef.update(
                mapOf(
                    "reviews" to currentReviews,
                    "rating" to newRating
                )
            ).await()
        }
        catch (e:Exception){
            Log.e("Failed to delte a review","${e.message}")
        }
    }
}